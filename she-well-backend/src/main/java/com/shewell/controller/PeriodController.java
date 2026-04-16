package com.shewell.controller;

import com.shewell.entity.*;
import com.shewell.service.*;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/period")
public class PeriodController {

    @Autowired private PeriodRecordService periodRecordService;
    @Autowired private PeriodPredictionService periodPredictionService;
    @Autowired private PeriodStatsService periodStatsService;
    @Autowired private BbtTempRecordService bbtTempRecordService;
    @Autowired private OvulationRecordService ovulationRecordService;
    @Autowired private IntercourseRecordService intercourseRecordService;

    // ===== 经期记录 =====
    @PostMapping("/record")
    public Result<PeriodRecord> addRecord(@RequestBody PeriodRecord record, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        record.setUserId(userId);
        record.setIsPhysicalDelete(0);
        periodRecordService.save(record);
        return Result.ok(record);
    }

    @GetMapping("/record/list")
    public Result<List<PeriodRecord>> getRecords(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<PeriodRecord> records = periodRecordService.lambdaQuery()
            .eq(PeriodRecord::getUserId, userId)
            .eq(PeriodRecord::getIsPhysicalDelete, 0)
            .orderByDesc(PeriodRecord::getStartDate)
            .list();
        return Result.ok(records);
    }

    @PutMapping("/record/{id}")
    public Result<Void> updateRecord(@PathVariable Long id, @RequestBody PeriodRecord record) {
        record.setId(id);
        periodRecordService.updateById(record);
        return Result.ok();
    }

    @DeleteMapping("/record/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        PeriodRecord record = periodRecordService.getById(id);
        record.setIsPhysicalDelete(1);
        periodRecordService.updateById(record);
        return Result.ok();
    }

    // ===== 经期预测 =====
    @GetMapping("/prediction")
    public Result<PeriodPrediction> getPrediction(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PeriodPrediction prediction = periodPredictionService.lambdaQuery()
            .eq(PeriodPrediction::getUserId, userId).one();
        return Result.ok(prediction);
    }

    @PostMapping("/prediction/calculate")
    public Result<PeriodPrediction> calculatePrediction(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        // 查询用户最近的经期记录（最多6条）
        List<PeriodRecord> records = periodRecordService.lambdaQuery()
            .eq(PeriodRecord::getUserId, userId)
            .eq(PeriodRecord::getIsPhysicalDelete, 0)
            .orderByDesc(PeriodRecord::getStartDate)
            .last("LIMIT 6").list();

        if (records.isEmpty()) {
            return Result.fail("暂无经期记录，请先记录经期数据");
        }

        PeriodPrediction pp = periodPredictionService.lambdaQuery()
            .eq(PeriodPrediction::getUserId, userId).one();
        if (pp == null) {
            pp = new PeriodPrediction();
            pp.setUserId(userId);
        }

        // 最近一次经期
        PeriodRecord latest = records.get(0);
        LocalDate lastStart = latest.getStartDate();
        int avgDuration = latest.getDuration() != null ? latest.getDuration() : 5;

        // 计算平均周期长度
        int avgCycle = 28;
        if (records.size() >= 2) {
            int totalDays = 0;
            int count = 0;
            for (int i = 0; i < records.size() - 1; i++) {
                if (records.get(i).getStartDate() != null && records.get(i + 1).getStartDate() != null) {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(
                        records.get(i + 1).getStartDate(), records.get(i).getStartDate());
                    if (days > 15 && days < 60) { // 排除异常值
                        totalDays += days;
                        count++;
                    }
                }
            }
            if (count > 0) avgCycle = totalDays / count;

            // 如果有多条记录，计算平均经期天数
            int totalDuration = 0;
            int durationCount = 0;
            for (PeriodRecord r : records) {
                if (r.getDuration() != null && r.getDuration() > 0) {
                    totalDuration += r.getDuration();
                    durationCount++;
                }
            }
            if (durationCount > 0) avgDuration = totalDuration / durationCount;
        }

        // 预测下次经期
        LocalDate nextStart = lastStart.plusDays(avgCycle);
        LocalDate nextEnd = nextStart.plusDays(avgDuration - 1);

        // 排卵日 = 下次经期前14天
        LocalDate ovulationDate = nextStart.minusDays(14);

        // 易孕期 = 排卵日前5天到排卵日后1天
        LocalDate fertileStart = ovulationDate.minusDays(5);
        LocalDate fertileEnd = ovulationDate.plusDays(1);

        // 计算置信度：基于历史记录数量和规律性
        java.math.BigDecimal confidence = new java.math.BigDecimal("0.60");
        if (records.size() >= 3) confidence = new java.math.BigDecimal("0.75");
        if (records.size() >= 5) confidence = new java.math.BigDecimal("0.85");
        if (records.size() >= 2) {
            // 检查规律性
            double variance = 0;
            int count = 0;
            double avg = avgCycle;
            for (int i = 0; i < records.size() - 1; i++) {
                if (records.get(i).getStartDate() != null && records.get(i + 1).getStartDate() != null) {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(
                        records.get(i + 1).getStartDate(), records.get(i).getStartDate());
                    if (days > 15 && days < 60) {
                        variance += Math.pow(days - avg, 2);
                        count++;
                    }
                }
            }
            if (count > 0) {
                double stdDev = Math.sqrt(variance / count);
                if (stdDev < 2) confidence = confidence.add(new java.math.BigDecimal("0.10"));
                else if (stdDev > 5) confidence = confidence.subtract(new java.math.BigDecimal("0.10"));
            }
        }

        pp.setCycleLength(avgCycle);
        pp.setPeriodLength(avgDuration);
        pp.setPredictedStartDate(lastStart);
        pp.setPredictedEndDate(lastStart.plusDays(avgDuration - 1));
        pp.setPredictedNextDate(nextStart);
        pp.setOvulationDate(ovulationDate);
        pp.setFertileStartDate(fertileStart);
        pp.setFertileEndDate(fertileEnd);
        pp.setPredictionConfidence(confidence);
        pp.setLastCalculated(LocalDateTime.now());
        periodPredictionService.saveOrUpdate(pp);
        return Result.ok(pp);
    }

    // ===== 经期统计 =====
    @GetMapping("/stats")
    public Result<List<PeriodStats>> getStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<PeriodStats> stats = periodStatsService.lambdaQuery()
            .eq(PeriodStats::getUserId, userId)
            .orderByDesc(PeriodStats::getStatMonth).list();
        return Result.ok(stats);
    }

    // ===== 基础体温 =====
    @PostMapping("/bbt")
    public Result<BbtTempRecord> addBbt(@RequestBody BbtTempRecord record, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        record.setUserId(userId);
        bbtTempRecordService.save(record);
        return Result.ok(record);
    }

    @GetMapping("/bbt/list")
    public Result<List<BbtTempRecord>> getBbtList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<BbtTempRecord> records = bbtTempRecordService.lambdaQuery()
            .eq(BbtTempRecord::getUserId, userId)
            .orderByDesc(BbtTempRecord::getMeasureDate)
            .list();
        return Result.ok(records);
    }

    // ===== 排卵记录 =====
    @PostMapping("/ovulation")
    public Result<OvulationRecord> addOvulation(@RequestBody OvulationRecord record, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        record.setUserId(userId);
        ovulationRecordService.save(record);
        return Result.ok(record);
    }

    @GetMapping("/ovulation/list")
    public Result<List<OvulationRecord>> getOvulationList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<OvulationRecord> records = ovulationRecordService.lambdaQuery()
            .eq(OvulationRecord::getUserId, userId)
            .orderByDesc(OvulationRecord::getRecordDate).list();
        return Result.ok(records);
    }

    // ===== 同房记录 =====
    @PostMapping("/intercourse")
    public Result<IntercourseRecord> addIntercourse(@RequestBody IntercourseRecord record, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        record.setUserId(userId);
        intercourseRecordService.save(record);
        return Result.ok(record);
    }

    @GetMapping("/intercourse/list")
    public Result<List<IntercourseRecord>> getIntercourseList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<IntercourseRecord> records = intercourseRecordService.lambdaQuery()
            .eq(IntercourseRecord::getUserId, userId)
            .orderByDesc(IntercourseRecord::getIntercourseDate).list();
        return Result.ok(records);
    }
}
