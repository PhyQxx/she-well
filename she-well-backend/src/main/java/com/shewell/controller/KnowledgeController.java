package com.shewell.controller;

import com.shewell.entity.*;
import com.shewell.service.*;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    @Autowired private KnowledgeCategoryService categoryService;
    @Autowired private KnowledgeArticleService articleService;
    @Autowired private UserReadHistoryService readHistoryService;
    @Autowired private UserCollectService userCollectService;

    @GetMapping("/categories")
    public Result<List<KnowledgeCategory>> getCategories() {
        return Result.ok(categoryService.lambdaQuery()
            .eq(KnowledgeCategory::getParentId, 0)
            .orderByAsc(KnowledgeCategory::getSortOrder).list());
    }

    @GetMapping("/categories/children")
    public Result<List<KnowledgeCategory>> getChildren(@RequestParam Long parentId) {
        return Result.ok(categoryService.lambdaQuery()
            .eq(KnowledgeCategory::getParentId, parentId)
            .orderByAsc(KnowledgeCategory::getSortOrder).list());
    }

    @GetMapping("/articles")
    public Result<List<KnowledgeArticle>> getArticles(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        var query = articleService.lambdaQuery()
            .eq(categoryId != null, KnowledgeArticle::getCategoryId, categoryId)
            .eq(KnowledgeArticle::getStatus, 1)
            .orderByDesc(KnowledgeArticle::getPublishTime);
        long total = query.count();
        query.last("LIMIT " + (page - 1) * size + "," + size);
        var records = new java.util.LinkedHashMap<String, Object>();
        records.put("records", query.list());
        records.put("total", total);
        records.put("page", page);
        records.put("size", size);
        return Result.ok((List) query.list());
    }

    @GetMapping("/article/{id}")
    public Result<KnowledgeArticle> getArticle(@PathVariable Long id) {
        KnowledgeArticle article = articleService.getById(id);
        articleService.lambdaUpdate().setSql("read_count = read_count + 1").eq(KnowledgeArticle::getId, id).update();
        return Result.ok(article);
    }

    @PostMapping("/article/{id}/read")
    public Result<Void> markRead(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserReadHistory history = new UserReadHistory();
        history.setUserId(userId);
        history.setTargetType("article");
        history.setTargetId(id);
        history.setReadAt(java.time.LocalDateTime.now());
        readHistoryService.save(history);
        return Result.ok();
    }

    @PostMapping("/article/{id}/like")
    public Result<Void> likeArticle(@PathVariable Long id) {
        articleService.lambdaUpdate().setSql("like_count = like_count + 1").eq(KnowledgeArticle::getId, id).update();
        return Result.ok();
    }

    @PostMapping("/article/{id}/collect")
    public Result<Void> collectArticle(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserCollect collect = new UserCollect();
        collect.setUserId(userId);
        collect.setTargetType("article");
        collect.setTargetId(id);
        userCollectService.save(collect);
        articleService.lambdaUpdate().setSql("collect_count = collect_count + 1").eq(KnowledgeArticle::getId, id).update();
        return Result.ok();
    }

    // ===== 管理端文章 CRUD =====
    @PostMapping("/article")
    public Result<KnowledgeArticle> createArticle(@RequestBody KnowledgeArticle article) {
        article.setPublishTime(java.time.LocalDateTime.now());
        article.setStatus(1);
        articleService.save(article);
        return Result.ok(article);
    }

    @PutMapping("/article/{id}")
    public Result<Void> updateArticle(@PathVariable Long id, @RequestBody KnowledgeArticle article) {
        article.setId(id);
        articleService.updateById(article);
        return Result.ok();
    }

    @DeleteMapping("/article/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        articleService.removeById(id);
        return Result.ok();
    }
}
