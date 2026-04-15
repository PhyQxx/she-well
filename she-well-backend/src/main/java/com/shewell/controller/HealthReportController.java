package com.shewell.controller;

import com.shewell.entity.HealthReport;
import com.shewell.service.HealthReportService;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/health-report")
@RequiredArgsConstructor
public class HealthReportController {

    private final HealthReportService healthReportService;

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
        report.put("avgCycleLength", 28);
        report.put("avgPeriodLength", 5);
        report.put("regularityScore", 85);
        report.put("symptomSummary", "近期体质良好，无明显异常");
        report.put("advice", "继续保持规律作息和均衡饮食");
        return Result.ok(report);
    }
}
