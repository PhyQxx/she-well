package com.shewell.controller;

import com.shewell.entity.User;
import com.shewell.entity.CommunityPost;
import com.shewell.entity.CheckinRecord;
import com.shewell.entity.KnowledgeArticle;
import com.shewell.entity.Settings;
import com.shewell.service.UserService;
import com.shewell.service.CommunityPostService;
import com.shewell.service.CheckinRecordService;
import com.shewell.service.KnowledgeArticleService;
import com.shewell.service.SettingsService;
import com.shewell.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final UserService userService;
    private final CommunityPostService postService;
    private final CheckinRecordService checkinService;
    private final KnowledgeArticleService articleService;
    private final SettingsService settingsService;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);

        long totalUsers = userService.count();
        long newUsersToday = userService.lambdaQuery()
            .ge(User::getCreateTime, today.toString() + " 00:00:00").count();
        long newUsersWeek = userService.lambdaQuery()
            .ge(User::getCreateTime, weekAgo.toString() + " 00:00:00").count();
        long totalPosts = postService.count();
        long todayPosts = postService.lambdaQuery()
            .ge(CommunityPost::getCreateTime, today.toString() + " 00:00:00").count();
        long totalArticles = articleService.count();
        long todayArticles = articleService.lambdaQuery()
            .ge(KnowledgeArticle::getCreateTime, today.toString() + " 00:00:00").count();
        long todayCheckins = checkinService.lambdaQuery()
            .ge(CheckinRecord::getCreateTime, today.toString() + " 00:00:00").count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("users", Map.of("total", totalUsers, "today", newUsersToday, "week", newUsersWeek));
        result.put("content", Map.of(
            "posts", Map.of("total", totalPosts, "today", todayPosts),
            "articles", Map.of("total", totalArticles, "today", todayArticles)
        ));
        result.put("checkin", Map.of("today", todayCheckins));
        result.put("pending", Map.of("posts", postService.lambdaQuery()
            .eq(CommunityPost::getStatus, 0).count()));
        return Result.ok(result);
    }

    @GetMapping("/trend")
    public Result<Map<String, Object>> trend(@RequestParam(defaultValue = "7") int days) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(days - 1);
        java.util.List<Map<String, Object>> userTrend = new java.util.ArrayList<>();
        java.util.List<Map<String, Object>> postTrend = new java.util.ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate d = start.plusDays(i);
            String dateStr = d.toString();
            long userCount = userService.lambdaQuery()
                .likeRight(User::getCreateTime, dateStr).count();
            long postCount = postService.lambdaQuery()
                .likeRight(CommunityPost::getCreateTime, dateStr).count();
            userTrend.add(Map.of("date", dateStr, "count", userCount));
            postTrend.add(Map.of("date", dateStr, "count", postCount));
        }
        return Result.ok(Map.of("userTrend", userTrend, "postTrend", postTrend));
    }

    @GetMapping("/mode-distribution")
    public Result<Map<String, Object>> modeDistribution() {
        long period = settingsService.lambdaQuery().eq(Settings::getCurrentMode, "period").count();
        long trying = settingsService.lambdaQuery().eq(Settings::getCurrentMode, "trying").count();
        long pregnancy = settingsService.lambdaQuery().eq(Settings::getCurrentMode, "pregnancy").count();
        long nursing = settingsService.lambdaQuery().eq(Settings::getCurrentMode, "nursing").count();
        return Result.ok(Map.of("period", period, "trying", trying, "pregnancy", pregnancy, "nursing", nursing));
    }
}
