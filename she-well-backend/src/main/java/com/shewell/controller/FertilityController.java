package com.shewell.controller;

import com.shewell.entity.PeriodPrediction;
import com.shewell.entity.PeriodRecord;
import com.shewell.entity.Fertility;
import com.shewell.service.FertilityService;
import com.shewell.service.PeriodPredictionService;
import com.shewell.service.PeriodRecordService;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 生育力评估 / 安全期 / 易孕期 / 每日受孕几率
 * PRD: 3.5 安全期与易孕期 + 4.12.2 生育力评估
 */
@RestController
@RequestMapping("/api/fertility")
@RequiredArgsConstructor
public class FertilityController {

    private final FertilityService fertilityService;
    private final PeriodPredictionService periodPredictionService;
    private final PeriodRecordService periodRecordService;

    // ===== 3.5.1 安全期计算 =====
    @GetMapping("/safe-period")
    public Result<Map<String, Object>> safePeriod(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LocalDate today = LocalDate.now();
        LocalDate fertileStart = today.plusDays(1);
        LocalDate fertileEnd = today.plusDays(5);
        // 经期第1-4天为绝对安全期
        LocalDate periodStart = today.minusDays(2);
        LocalDate periodEnd = today.plusDays(1);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("absoluteSafeStart", periodStart);
        result.put("absoluteSafeEnd", periodEnd);
        result.put("relativeSafeStart", fertileEnd.plusDays(1));
        result.put("relativeSafeEnd", today.plusDays(10));
        return Result.ok(result);
    }

    // ===== 3.5.2 易孕期提示 =====
    @GetMapping("/fertile-period")
    public Result<Map<String, Object>> fertilePeriod(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PeriodPrediction pred = periodPredictionService.lambdaQuery()
            .eq(PeriodPrediction::getUserId, userId).one();
        Map<String, Object> result = new LinkedHashMap<>();
        if (pred != null && pred.getOvulationDate() != null) {
            LocalDate ovulation = pred.getOvulationDate();
            result.put("fertileStart", ovulation.minusDays(5));
            result.put("fertileEnd", ovulation.plusDays(1));
            result.put("peakOvulation", ovulation);
            result.put("peakDays", List.of(ovulation.minusDays(1), ovulation, ovulation.plusDays(1)));
        } else {
            // 无预测数据时返回估算
            result.put("fertileStart", LocalDate.now().plusDays(10));
            result.put("fertileEnd", LocalDate.now().plusDays(16));
            result.put("peakOvulation", LocalDate.now().plusDays(14));
        }
        return Result.ok(result);
    }

    // ===== 3.5.3 生育力评估（PRD 4.12.2 规范）=====
    @GetMapping("/assessment")
    public Result<Map<String, Object>> assessment(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        // 从历史记录分析
        List<PeriodRecord> records = periodRecordService.lambdaQuery()
            .eq(PeriodRecord::getUserId, userId)
            .orderByDesc(PeriodRecord::getStartDate)
            .last("LIMIT 6").list();
        String level = "medium";
        List<String> factors = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        if (records.size() >= 3) {
            // 分析周期规律性
            List<Integer> cycles = new ArrayList<>();
            for (int i = 0; i < records.size() - 1; i++) {
                if (records.get(i).getStartDate() != null && records.get(i + 1).getStartDate() != null) {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(
                        records.get(i + 1).getStartDate(), records.get(i).getStartDate());
                    cycles.add((int) days);
                }
            }
            if (!cycles.isEmpty()) {
                double avg = cycles.stream().mapToInt(Integer::intValue).average().orElse(28);
                double variance = cycles.stream()
                    .mapToDouble(c -> Math.pow(c - avg, 2)).average().orElse(0);
                if (variance > 25) {
                    level = "low";
                    factors.add("周期不规律，波动较大（标准差>" + (int) Math.sqrt(variance) + "天）");
                    suggestions.add("建议记录至少3个月经期数据以提高预测准确度");
                } else if (avg < 25 || avg > 35) {
                    level = "low";
                    factors.add("平均周期长度" + (int) avg + "天，偏离标准范围（25-35天）");
                    suggestions.add("周期过短或过长建议咨询妇科医生");
                } else {
                    level = "medium";
                    factors.add("周期基本规律，平均" + (int) avg + "天");
                }
            }
        } else {
            level = "medium";
            factors.add("历史记录不足，建议持续记录至少3个周期");
            suggestions.add("记录更多经期数据可获得更准确的生育力评估");
        }
        if (level.equals("medium") && factors.size() <= 1) {
            level = "high";
            factors.clear();
            factors.add("周期规律，暂无明显异常");
            suggestions.add("保持现有健康生活方式");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("level", level);
        result.put("factors", factors.subList(0, Math.min(factors.size(), 3)));
        result.put("suggestions", suggestions.subList(0, Math.min(suggestions.size(), 3)));
        return Result.ok(result);
    }

    // ===== 每日受孕几率 =====
    @GetMapping("/daily-chance")
    public Result<Map<String, Object>> dailyChance(
            @RequestParam(required = false) LocalDate date,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (date == null) date = LocalDate.now();
        PeriodPrediction pred = periodPredictionService.lambdaQuery()
            .eq(PeriodPrediction::getUserId, userId).one();
        String chance;
        if (pred != null && pred.getOvulationDate() != null) {
            long daysToOvulation = java.time.temporal.ChronoUnit.DAYS.between(date, pred.getOvulationDate());
            if (daysToOvulation >= -2 && daysToOvulation <= 1) {
                chance = "high";
            } else if (daysToOvulation >= -5 && daysToOvulation <= 5) {
                chance = "medium";
            } else {
                chance = "low";
            }
        } else {
            chance = "low";
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", date);
        result.put("chance", chance);
        result.put("description", switch (chance) {
            case "high" -> "今日受孕几率较高";
            case "medium" -> "今日受孕几率一般";
            default -> "今日受孕几率较低";
        });
        return Result.ok(result);
    }
}
