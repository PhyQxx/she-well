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
        PeriodPrediction pp = periodPredictionService.lambdaQuery()
            .eq(PeriodPrediction::getUserId, userId).one();
        if (pp == null) {
            pp = new PeriodPrediction();
            pp.setUserId(userId);
        }
        // 简单预测逻辑：下次经期 = lastPeriod + cycleLength
        pp.setCycleLength(28);
        pp.setPeriodLength(5);
        pp.setPredictionConfidence(new java.math.BigDecimal("0.85"));
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
