package com.shewell.controller;

import com.shewell.entity.PeriodStats;
import com.shewell.entity.CheckinRecord;
import com.shewell.entity.PeriodRecord;
import com.shewell.service.PeriodStatsService;
import com.shewell.service.CheckinRecordService;
import com.shewell.service.PeriodRecordService;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final PeriodStatsService periodStatsService;
    private final CheckinRecordService checkinRecordService;
    private final PeriodRecordService periodRecordService;

    @GetMapping("/period/monthly")
    public Result<List<PeriodStats>> monthlyStats(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (year == null) year = LocalDate.now().getYear();
        if (month == null) month = LocalDate.now().getMonthValue();
        String statMonth = year + "-" + String.format("%02d", month);
        return Result.ok(periodStatsService.lambdaQuery()
            .eq(PeriodStats::getUserId, userId)
            .eq(PeriodStats::getStatMonth, statMonth)
            .list());
    }

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> vo = new LinkedHashMap<>();
        List<PeriodRecord> records = periodRecordService.lambdaQuery()
            .eq(PeriodRecord::getUserId, userId)
            .orderByDesc(PeriodRecord::getStartDate)
            .last("LIMIT 6").list();
        if (!records.isEmpty()) {
            List<Integer> cycles = new ArrayList<>();
            List<Integer> durations = new ArrayList<>();
            for (int i = 0; i < records.size() - 1; i++) {
                PeriodRecord curr = records.get(i);
                PeriodRecord prev = records.get(i + 1);
                if (curr.getStartDate() != null && prev.getStartDate() != null) {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(prev.getStartDate(), curr.getStartDate());
                    cycles.add((int) days);
                }
                if (curr.getDuration() != null) durations.add(curr.getDuration());
            }
            if (!cycles.isEmpty()) {
                double avgCycle = cycles.stream().mapToInt(Integer::intValue).average().orElse(28);
                vo.put("averageCycle", (int) avgCycle);
            }
            if (!durations.isEmpty()) {
                double avgDuration = durations.stream().mapToInt(Integer::intValue).average().orElse(5);
                vo.put("averageDuration", (int) avgDuration);
            }
        }
        long totalCheckins = checkinRecordService.lambdaQuery()
            .eq(CheckinRecord::getUserId, userId).count();
        long periodCount = periodRecordService.lambdaQuery()
            .eq(PeriodRecord::getUserId, userId).count();
        vo.put("totalCheckins", (int) totalCheckins);
        vo.put("periodCount", (int) periodCount);
        return Result.ok(vo);
    }

    @GetMapping("/cycle/trend")
    public Result<Map<String, Object>> cycleTrend(
            @RequestParam(defaultValue = "6") int months,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<PeriodRecord> records = periodRecordService.lambdaQuery()
            .eq(PeriodRecord::getUserId, userId)
            .orderByDesc(PeriodRecord::getStartDate)
            .last("LIMIT " + (months + 1)).list();
        Collections.reverse(records);
        List<Map<String, Object>> trend = new ArrayList<>();
        for (int i = 0; i < records.size() - 1; i++) {
            PeriodRecord curr = records.get(i);
            PeriodRecord next = records.get(i + 1);
            if (curr.getStartDate() != null && next.getStartDate() != null) {
                long cycleLen = java.time.temporal.ChronoUnit.DAYS.between(next.getStartDate(), curr.getStartDate());
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("month", curr.getStartDate().getYear() + "-" + String.format("%02d", curr.getStartDate().getMonthValue()));
                item.put("cycleLength", cycleLen);
                item.put("periodLength", curr.getDuration() != null ? curr.getDuration() : 0);
                trend.add(item);
            }
        }
        return Result.ok(Map.of("trend", trend));
    }

    @GetMapping("/checkin/summary")
    public Result<Map<String, Object>> checkinSummary(
            @RequestParam(defaultValue = "30") int days,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LocalDate start = LocalDate.now().minusDays(days);
        List<CheckinRecord> records = checkinRecordService.lambdaQuery()
            .eq(CheckinRecord::getUserId, userId)
            .ge(CheckinRecord::getCheckinDate, start)
            .orderByDesc(CheckinRecord::getCheckinDate)
            .list();
        Map<String, Long> byDate = records.stream()
            .collect(Collectors.groupingBy(
                c -> c.getCheckinDate() != null ? c.getCheckinDate().toString() : "",
                Collectors.counting()
            ));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", records);
        result.put("byDate", byDate);
        result.put("total", records.size());
        return Result.ok(result);
    }
}
