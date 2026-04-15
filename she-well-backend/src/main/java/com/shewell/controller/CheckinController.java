package com.shewell.controller;

import com.shewell.entity.*;
import com.shewell.service.*;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkin")
public class CheckinController {

    @Autowired private HabitService habitService;
    @Autowired private CheckinRecordService checkinRecordService;
    @Autowired private BodyMetricsRecordService bodyMetricsRecordService;

    // ===== 习惯管理 =====
    @PostMapping("/habit")
    public Result<Habit> addHabit(@RequestBody Habit habit, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        habit.setUserId(userId);
        habitService.save(habit);
        return Result.ok(habit);
    }

    @GetMapping("/habit/list")
    public Result<List<Habit>> getHabits(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Habit> habits = habitService.lambdaQuery()
            .eq(Habit::getUserId, userId).list();
        return Result.ok(habits);
    }

    @PutMapping("/habit/{id}")
    public Result<Void> updateHabit(@PathVariable Long id, @RequestBody Habit habit) {
        habit.setId(id);
        habitService.updateById(habit);
        return Result.ok();
    }

    @DeleteMapping("/habit/{id}")
    public Result<Void> deleteHabit(@PathVariable Long id) {
        habitService.removeById(id);
        return Result.ok();
    }

    // ===== 打卡记录 =====
    @PostMapping("/record")
    public Result<CheckinRecord> addRecord(@RequestBody CheckinRecord record, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        record.setUserId(userId);
        checkinRecordService.save(record);
        return Result.ok(record);
    }

    @GetMapping("/record/list")
    public Result<List<CheckinRecord>> getRecords(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<CheckinRecord> records = checkinRecordService.lambdaQuery()
            .eq(CheckinRecord::getUserId, userId)
            .orderByDesc(CheckinRecord::getCheckinDate).list();
        return Result.ok(records);
    }

    @GetMapping("/record/today")
    public Result<List<CheckinRecord>> getTodayRecords(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<CheckinRecord> records = checkinRecordService.lambdaQuery()
            .eq(CheckinRecord::getUserId, userId)
            .eq(CheckinRecord::getCheckinDate, java.time.LocalDate.now()).list();
        return Result.ok(records);
    }

    // ===== 体征记录 =====
    @PostMapping("/metrics")
    public Result<BodyMetricsRecord> addMetrics(@RequestBody BodyMetricsRecord record, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        record.setUserId(userId);
        bodyMetricsRecordService.save(record);
        return Result.ok(record);
    }

    @GetMapping("/metrics/list")
    public Result<List<BodyMetricsRecord>> getMetrics(
            @RequestParam String metricType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        var query = bodyMetricsRecordService.lambdaQuery()
            .eq(BodyMetricsRecord::getUserId, userId)
            .eq(BodyMetricsRecord::getMetricType, metricType);
        if (startDate != null) query.ge(BodyMetricsRecord::getRecordDate, java.time.LocalDate.parse(startDate));
        if (endDate != null) query.le(BodyMetricsRecord::getRecordDate, java.time.LocalDate.parse(endDate));
        return Result.ok(query.orderByDesc(BodyMetricsRecord::getRecordDate).list());
    }
}
