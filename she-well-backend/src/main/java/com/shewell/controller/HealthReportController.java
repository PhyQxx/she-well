package com.shewell.controller;

import com.shewell.entity.HealthReport;
import com.shewell.entity.PeriodRecord;
import com.shewell.entity.CheckinRecord;
import com.shewell.service.HealthReportService;
import com.shewell.service.PeriodRecordService;
import com.shewell.service.CheckinRecordService;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/health-report")
@RequiredArgsConstructor
public class HealthReportController {

    private final HealthReportService healthReportService;
    private final PeriodRecordService periodRecordService;
    private final CheckinRecordService checkinRecordService;

    @GetMapping("/list")
    public Result<?> list(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        var query = healthReportService.lambdaQuery().eq(HealthReport::getUserId, userId);
        if (period != null && !period.isBlank()) {
            query.eq(HealthReport::getPeriodStart, LocalDate.parse(period));
        }
        return Result.ok(query.orderByDesc(HealthReport::getCreateTime).list());
    }

    @GetMapping("/{id}")
    public Result<HealthReport> get(@PathVariable Long id, HttpServletRequest request) {
        HealthReport report = healthReportService.lambdaQuery()
            .eq(HealthReport::getId, id)
            .one();
        return report != null ? Result.ok(report) : Result.fail("报告不存在");
    }

    @PostMapping("/generate")
    public Result<Map<String, Object>> generate(
            @RequestParam(defaultValue = "weekly") String type,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("type", type);
        report.put("generatedAt", LocalDate.now());

        // 查询经期记录
        List<PeriodRecord> periodRecords = periodRecordService.lambdaQuery()
            .eq(PeriodRecord::getUserId, userId)
            .eq(PeriodRecord::getIsPhysicalDelete, 0)
            .orderByDesc(PeriodRecord::getStartDate)
            .last("LIMIT 6").list();

        // 计算平均周期
        int avgCycleLength = 28;
        int avgPeriodLength = 5;
        int regularityScore = 50;

        if (!periodRecords.isEmpty()) {
            // 平均经期天数
            avgPeriodLength = (int) periodRecords.stream()
                .filter(r -> r.getDuration() != null && r.getDuration() > 0)
                .mapToInt(PeriodRecord::getDuration)
                .average().orElse(5);

            // 平均周期长度和规律性
            if (periodRecords.size() >= 2) {
                List<Long> cycles = new ArrayList<>();
                for (int i = 0; i < periodRecords.size() - 1; i++) {
                    if (periodRecords.get(i).getStartDate() != null && periodRecords.get(i + 1).getStartDate() != null) {
                        long days = ChronoUnit.DAYS.between(
                            periodRecords.get(i + 1).getStartDate(), periodRecords.get(i).getStartDate());
                        if (days > 15 && days < 60) cycles.add(days);
                    }
                }
                if (!cycles.isEmpty()) {
                    avgCycleLength = (int) cycles.stream().mapToLong(Long::longValue).average().orElse(28);
                    // 规律性评分：标准差越小越规律
                    double avg = avgCycleLength;
                    double variance = cycles.stream().mapToDouble(c -> Math.pow(c - avg, 2)).average().orElse(0);
                    double stdDev = Math.sqrt(variance);
                    // 标准差 0 → 100分，标准差 7+ → 30分
                    regularityScore = Math.max(30, Math.min(100, (int) (100 - stdDev * 10)));
                }
            }
        }

        report.put("avgCycleLength", avgCycleLength);
        report.put("avgPeriodLength", avgPeriodLength);
        report.put("regularityScore", regularityScore);

        // 查询近期打卡记录，汇总症状
        LocalDate sinceDate = "weekly".equals(type) ? LocalDate.now().minusDays(7) : LocalDate.now().minusMonths(1);
        List<CheckinRecord> checkins = checkinRecordService.lambdaQuery()
            .eq(CheckinRecord::getUserId, userId)
            .ge(CheckinRecord::getCheckinDate, sinceDate)
            .orderByDesc(CheckinRecord::getCheckinDate).list();

        String symptomSummary;
        if (checkins.isEmpty()) {
            symptomSummary = "近期暂无打卡记录";
        } else {
            // 统计症状
            Map<String, Long> symptomCounts = checkins.stream()
                .filter(c -> c.getSymptom() != null && !c.getSymptom().isEmpty())
                .flatMap(c -> Arrays.stream(c.getSymptom().split("[,，、]")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

            if (symptomCounts.isEmpty()) {
                symptomSummary = "近期体质良好，无明显异常症状";
            } else {
                String topSymptoms = symptomCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(3)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.joining("、"));
                symptomSummary = "近期主要症状：" + topSymptoms;
            }
        }
        report.put("symptomSummary", symptomSummary);

        // 健康建议
        StringBuilder advice = new StringBuilder();
        if (regularityScore >= 80) {
            advice.append("您的周期非常规律，继续保持规律作息和均衡饮食。");
        } else if (regularityScore >= 60) {
            advice.append("您的周期基本规律，建议注意作息规律，避免熬夜和过度劳累。");
        } else {
            advice.append("您的周期波动较大，建议记录更多经期数据，必要时咨询妇科医生。");
        }
        if (checkins.size() < 3) {
            advice.append(" 建议每日打卡记录健康状况，有助于获得更准确的分析。");
        }
        report.put("advice", advice.toString());

        return Result.ok(report);
    }
}
