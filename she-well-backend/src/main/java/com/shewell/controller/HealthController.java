package com.shewell.controller;

import com.shewell.entity.*;
import com.shewell.service.*;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired private HealthReportService healthReportService;
    @Autowired private BabyService babyService;
    @Autowired private ActivityService activityService;

    // ===== 健康报告 =====
    @PostMapping("/report")
    public Result<HealthReport> generateReport(@RequestBody HealthReport report, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        report.setUserId(userId);
        healthReportService.save(report);
        return Result.ok(report);
    }

    @GetMapping("/reports")
    public Result<List<HealthReport>> getReports(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(healthReportService.lambdaQuery()
            .eq(HealthReport::getUserId, userId)
            .orderByDesc(HealthReport::getCreateTime).list());
    }

    // ===== 宝宝 =====
    @GetMapping("/babies")
    public Result<List<Baby>> getBabies(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(babyService.lambdaQuery()
            .eq(Baby::getUserId, userId)
            .orderByAsc(Baby::getBirthDate).list());
    }

    @GetMapping("/baby/{id}")
    public Result<Baby> getBaby(@PathVariable Long id) {
        return Result.ok(babyService.getById(id));
    }

    // ===== 活动 =====
    @GetMapping("/activities")
    public Result<List<Activity>> getActivities() {
        return Result.ok(activityService.lambdaQuery()
            .eq(Activity::getStatus, "published")
            .orderByDesc(Activity::getCreateTime).list());
    }

    @GetMapping("/activity/{id}")
    public Result<Activity> getActivity(@PathVariable Long id) {
        return Result.ok(activityService.getById(id));
    }

    @PostMapping("/activity/{id}/join")
    public Result<Void> joinActivity(@PathVariable Long id) {
        activityService.lambdaUpdate()
            .setSql("participant_count = participant_count + 1")
            .eq(Activity::getId, id).update();
        return Result.ok();
    }
}
