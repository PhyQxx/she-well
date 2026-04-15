package com.shewell.controller;

import com.shewell.entity.Achievement;
import com.shewell.entity.UserAchievement;
import com.shewell.service.AchievementService;
import com.shewell.service.UserAchievementService;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/achievement")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final UserAchievementService userAchievementService;

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
        // 演示：检查成就并解锁新获得的
        List<Achievement> allAchievements = achievementService.list();
        return Result.ok(List.of()); // 返回本次新解锁的成就
    }
}
