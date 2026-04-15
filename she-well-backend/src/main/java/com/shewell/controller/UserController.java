package com.shewell.controller;

import com.shewell.entity.*;
import com.shewell.service.*;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private UserProfileService userProfileService;
    @Autowired private UserSettingsService userSettingsService;
    @Autowired private UserFollowService userFollowService;
    @Autowired private UserAchievementService userAchievementService;
    @Autowired private AchievementService achievementService;

    @GetMapping("/profile")
    public Result<UserProfile> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserProfile profile = userProfileService.lambdaQuery().eq(UserProfile::getUserId, userId).one();
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setMenstrualCycle(28);
            profile.setPeriodDuration(5);
            userProfileService.save(profile);
        }
        return Result.ok(profile);
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UserProfile profile, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        profile.setUserId(userId);
        userProfileService.lambdaUpdate().eq(UserProfile::getUserId, userId).update(profile);
        return Result.ok();
    }

    @GetMapping("/settings")
    public Result<UserSettings> getSettings(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserSettings settings = userSettingsService.lambdaQuery().eq(UserSettings::getUserId, userId).one();
        if (settings == null) {
            settings = new UserSettings();
            settings.setUserId(userId);
            settings.setReminderEnabled(1);
            settings.setReminderTime("09:00");
            settings.setPeriodReminderDays(3);
            settings.setOvulReminder(1);
            settings.setPushEnabled(1);
            settings.setTheme("light");
            settings.setLanguage("zh_CN");
            userSettingsService.save(settings);
        }
        return Result.ok(settings);
    }

    @PutMapping("/settings")
    public Result<Void> updateSettings(@RequestBody UserSettings settings, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        settings.setUserId(userId);
        userSettingsService.lambdaUpdate().eq(UserSettings::getUserId, userId).update(settings);
        return Result.ok();
    }

    @GetMapping("/info/{id}")
    public Result<UserInfoVO> getUserInfo(@PathVariable Long id) {
        User user = userService.getById(id);
        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        return Result.ok(vo);
    }

    @PostMapping("/follow/{targetId}")
    public Result<Void> follow(@PathVariable Long targetId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserFollow follow = new UserFollow();
        follow.setUserId(userId);
        follow.setTargetUserId(targetId);
        userFollowService.save(follow);
        return Result.ok();
    }

    @DeleteMapping("/follow/{targetId}")
    public Result<Void> unfollow(@PathVariable Long targetId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userFollowService.lambdaUpdate()
            .eq(UserFollow::getUserId, userId)
            .eq(UserFollow::getTargetUserId, targetId)
            .remove();
        return Result.ok();
    }

    @GetMapping("/achievements")
    public Result<UserAchievementVO> getAchievements(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        var userAchs = userAchievementService.lambdaQuery().eq(UserAchievement::getUserId, userId).list();
        var allAchs = achievementService.list();
        UserAchievementVO vo = new UserAchievementVO();
        vo.setUserAchievements(userAchs);
        vo.setAllAchievements(allAchs);
        return Result.ok(vo);
    }

    @Data public static class UserInfoVO { private Long userId; private String nickname; private String avatar; }
    @Data public static class UserAchievementVO { private java.util.List<UserAchievement> userAchievements; private java.util.List<Achievement> allAchievements; }
}
