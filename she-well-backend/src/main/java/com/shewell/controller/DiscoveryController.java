package com.shewell.controller;

import com.shewell.entity.KnowledgeArticle;
import com.shewell.entity.Banner;
import com.shewell.service.*;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/discovery")
@RequiredArgsConstructor
public class DiscoveryController {

    private final BannerService bannerService;
    private final KnowledgeArticleService articleService;
    private final NotificationService notificationService;
    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/banners")
    public Result<List<Banner>> banners() {
        @SuppressWarnings("unchecked")
        List<Banner> cached = (List<Banner>) redisTemplate.opsForValue().get("banner:list");
        if (cached != null) return Result.ok(cached);
        List<Banner> banners = bannerService.lambdaQuery()
            .eq(Banner::getStatus, 1)
            .orderByAsc(Banner::getSortOrder).list();
        redisTemplate.opsForValue().set("banner:list", banners, 5, TimeUnit.MINUTES);
        return Result.ok(banners);
    }

    @GetMapping("/hot-articles")
    public Result<List<KnowledgeArticle>> hotArticles(
            @RequestParam(defaultValue = "10") int limit) {
        @SuppressWarnings("unchecked")
        List<KnowledgeArticle> cached = (List<KnowledgeArticle>) redisTemplate.opsForValue().get("hot:articles");
        if (cached != null) return Result.ok(cached);
        List<KnowledgeArticle> articles = articleService.lambdaQuery()
            .eq(KnowledgeArticle::getStatus, 1)
            .orderByDesc(KnowledgeArticle::getReadCount)
            .last("LIMIT " + limit).list();
        redisTemplate.opsForValue().set("hot:articles", articles, 1, TimeUnit.HOURS);
        return Result.ok(articles);
    }

    @GetMapping("/recommend-articles")
    public Result<List<KnowledgeArticle>> recommend(
            @RequestParam(defaultValue = "5") int limit,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<KnowledgeArticle> articles = articleService.lambdaQuery()
            .eq(KnowledgeArticle::getStatus, 1)
            .orderByDesc(KnowledgeArticle::getCreateTime)
            .last("LIMIT " + limit).list();
        return Result.ok(articles);
    }
}
