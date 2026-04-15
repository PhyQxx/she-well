package com.shewell.controller;

import com.shewell.entity.Reminder;
import com.shewell.service.ReminderService;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 健康提醒 CRUD
 * PRD: 3.6.3 健康提醒
 */
@RestController
@RequestMapping("/api/reminder")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping
    public Result<Reminder> create(@RequestBody Reminder reminder, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        reminder.setUserId(userId);
        reminder.setEnabled(true);
        reminder.setCreateTime(LocalDateTime.now());
        reminderService.save(reminder);
        return Result.ok(reminder);
    }

    @GetMapping("/list")
    public Result<?> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(reminderService.lambdaQuery()
            .eq(Reminder::getUserId, userId)
            .orderByAsc(Reminder::getRemindTime)
            .list());
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Reminder reminder) {
        reminder.setId(id);
        reminder.setUpdateTime(LocalDateTime.now());
        reminderService.updateById(reminder);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        reminderService.removeById(id);
        return Result.ok();
    }

    @PutMapping("/{id}/toggle")
    public Result<Void> toggle(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Reminder reminder = reminderService.getById(id);
        if (reminder != null) {
            reminder.setEnabled(body.getOrDefault("enabled", true));
            reminder.setUpdateTime(LocalDateTime.now());
            reminderService.updateById(reminder);
        }
        return Result.ok();
    }
}
