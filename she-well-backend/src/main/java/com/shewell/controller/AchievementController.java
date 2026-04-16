package com.shewell.controller;

import com.shewell.entity.Achievement;
import com.shewell.entity.UserAchievement;
import com.shewell.entity.CheckinRecord;
import com.shewell.entity.PeriodRecord;
import com.shewell.entity.CommunityPost;
import com.shewell.service.AchievementService;
import com.shewell.service.UserAchievementService;
import com.shewell.service.CheckinRecordService;
import com.shewell.service.PeriodRecordService;
import com.shewell.service.CommunityPostService;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/achievement")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final UserAchievementService userAchievementService;
    private final CheckinRecordService checkinRecordService;
    private final PeriodRecordService periodRecordService;
    private final CommunityPostService communityPostService;

    @GetMapping("/list")
    public Result<List<Achievement>> allAchievements() {
        return Result.ok(achievementService.lambdaQuery().orderByAsc(Achievement::getId).list());
    }

    @GetMapping("/my")
    public Result<List<UserAchievement>> myAchievements(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(userAchievementService.lambdaQuery()
            .eq(UserAchievement::getUserId, userId)
            .orderByDesc(UserAchievement::getEarnedTime)
            .list());
    }

    @PostMapping("/check")
    public Result<List<Achievement>> checkAndUnlock(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Achievement> allAchievements = achievementService.list();
        List<UserAchievement> earned = userAchievementService.lambdaQuery()
            .eq(UserAchievement::getUserId, userId).list();
        Set<Long> earnedIds = new HashSet<>();
        for (UserAchievement ua : earned) earnedIds.add(ua.getAchievementId());

        // 获取用户活动数据
        long totalCheckins = checkinRecordService.lambdaQuery()
            .eq(CheckinRecord::getUserId, userId).count();
        long totalPeriodRecords = periodRecordService.lambdaQuery()
            .eq(PeriodRecord::getUserId, userId)
            .eq(PeriodRecord::getIsPhysicalDelete, 0).count();
        long totalPosts = communityPostService.lambdaQuery()
            .eq(CommunityPost::getUserId, userId).count();

        List<Achievement> newlyUnlocked = new ArrayList<>();

        for (Achievement a : allAchievements) {
            if (earnedIds.contains(a.getId())) continue;
            if (checkCondition(a.getConditionType(), a.getConditionValue(),
                    totalCheckins, totalPeriodRecords, totalPosts)) {
                // 解锁成就
                UserAchievement ua = new UserAchievement();
                ua.setUserId(userId);
                ua.setAchievementId(a.getId());
                ua.setStatus(1);
                ua.setProgressCurrent(Integer.parseInt(a.getConditionValue()));
                ua.setProgressTarget(Integer.parseInt(a.getConditionValue()));
                ua.setEarnedTime(LocalDateTime.now());
                userAchievementService.save(ua);
                newlyUnlocked.add(a);
            }
        }
        return Result.ok(newlyUnlocked);
    }

    private boolean checkCondition(String type, String value,
                                    long totalCheckins, long totalPeriodRecords, long totalPosts) {
        if (type == null || value == null) return false;
        int target;
        try {
            target = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return switch (type) {
            case "checkin_count" -> totalCheckins >= target;
            case "period_record_count" -> totalPeriodRecords >= target;
            case "post_count" -> totalPosts >= target;
            default -> false;
        };
    }
}
