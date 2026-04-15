package com.shewell.controller;

import com.shewell.entity.Settings;
import com.shewell.entity.User;
import com.shewell.service.SettingsService;
import com.shewell.service.UserService;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;
    private final UserService userService;

    @GetMapping
    public Result<Settings> getSettings(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Settings settings = settingsService.lambdaQuery()
            .eq(Settings::getUserId, userId).one();
        if (settings == null) {
            settings = new Settings();
            settings.setUserId(userId);
            settings.setCurrentMode("period");
            settings.setCycleLength(28);
            settings.setPeriodDuration(5);
            settings.setNotifyEnabled(true);
            settings.setCycleReminderDays(1);
            settings.setCreateTime(LocalDateTime.now());
            settingsService.save(settings);
        }
        return Result.ok(settings);
    }

    @PutMapping
    public Result<Void> updateSettings(@RequestBody Settings settings, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Settings existing = settingsService.lambdaQuery()
            .eq(Settings::getUserId, userId).one();
        if (existing != null) {
            settings.setId(existing.getId());
        }
        settings.setUserId(userId);
        settings.setUpdateTime(LocalDateTime.now());
        settingsService.saveOrUpdate(settings);
        return Result.ok();
    }

    @PutMapping("/mode")
    public Result<Void> updateMode(@RequestBody java.util.Map<String, String> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String mode = body.get("mode");
        // 同步更新 User 表的 currentMode
        User user = userService.getById(userId);
        if (user != null) {
            user.setCurrentMode(mode);
            user.setUpdateTime(LocalDateTime.now());
            userService.updateById(user);
        }
        // 同步更新 user_mode 表
        Settings settings = settingsService.lambdaQuery()
            .eq(Settings::getUserId, userId).one();
        if (settings == null) {
            settings = new Settings();
            settings.setUserId(userId);
            settings.setCurrentMode(mode);
            settings.setCycleLength(28);
            settings.setPeriodDuration(5);
            settings.setCreateTime(LocalDateTime.now());
            settingsService.save(settings);
        } else {
            settings.setCurrentMode(mode);
            settings.setUpdateTime(LocalDateTime.now());
            settingsService.updateById(settings);
        }
        return Result.ok();
    }
}
