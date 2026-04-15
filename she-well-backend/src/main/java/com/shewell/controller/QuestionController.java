package com.shewell.controller;

import com.shewell.entity.CommunityQuestion;
import com.shewell.entity.User;
import com.shewell.service.CommunityQuestionService;
import com.shewell.service.UserService;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 姐妹问答 - 问题
 * PRD: 3.9.3 问答互助
 */
@RestController
@RequestMapping("/api/community/question")
@RequiredArgsConstructor
public class QuestionController {

    private final CommunityQuestionService questionService;
    private final UserService userService;

    @PostMapping
    public Result<CommunityQuestion> create(@RequestBody CommunityQuestion question, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        question.setUserId(userId);
        question.setViewCount(0);
        question.setAnswerCount(0);
        question.setLikeCount(0);
        question.setStatus("open");
        question.setCreateTime(LocalDateTime.now());
        questionService.save(question);
        return Result.ok(question);
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        var query = questionService.lambdaQuery();
        if (category != null && !category.isBlank()) {
            query.eq(CommunityQuestion::getCategory, category);
        }
        if (keyword != null && !keyword.isBlank()) {
            query.like(CommunityQuestion::getTitle, keyword);
        }
        List<CommunityQuestion> all = query.orderByDesc(CommunityQuestion::getCreateTime).list();
        int total = all.size();
        int from = (page - 1) * size;
        List<CommunityQuestion> paged = all.subList(Math.min(from, total), Math.min(from + size, total));
        // 填充用户信息
        List<Map<String, Object>> list = paged.stream().map(q -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", q.getId());
            item.put("title", q.getTitle());
            item.put("content", q.getContent());
            item.put("category", q.getCategory());
            item.put("viewCount", q.getViewCount());
            item.put("answerCount", q.getAnswerCount());
            item.put("likeCount", q.getLikeCount());
            item.put("status", q.getStatus());
            item.put("createTime", q.getCreateTime());
            User user = userService.getById(q.getUserId());
            item.put("user", user != null ? Map.of(
                "id", user.getId(),
                "nickname", user.getNickname() != null ? user.getNickname() : "",
                "avatar", user.getAvatar() != null ? user.getAvatar() : ""
            ) : null);
            return item;
        }).toList();
        return Result.ok(Map.of("list", list, "total", total));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> get(@PathVariable Long id, HttpServletRequest request) {
        CommunityQuestion question = questionService.getById(id);
        if (question == null) return Result.fail("问题不存在");
        question.setViewCount(question.getViewCount() == null ? 1 : question.getViewCount() + 1);
        questionService.updateById(question);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", question.getId());
        result.put("title", question.getTitle());
        result.put("content", question.getContent());
        result.put("category", question.getCategory());
        result.put("viewCount", question.getViewCount());
        result.put("answerCount", question.getAnswerCount());
        result.put("likeCount", question.getLikeCount());
        result.put("status", question.getStatus());
        result.put("createTime", question.getCreateTime());
        User user = userService.getById(question.getUserId());
        result.put("user", user != null ? Map.of(
            "id", user.getId(),
            "nickname", user.getNickname() != null ? user.getNickname() : "",
            "avatar", user.getAvatar() != null ? user.getAvatar() : ""
        ) : null);
        return Result.ok(result);
    }

    @PutMapping("/{id}/accept")
    public Result<Void> acceptAnswer(@PathVariable Long id,
            @RequestBody Map<String, Long> body,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        CommunityQuestion question = questionService.getById(id);
        if (question == null) return Result.fail("问题不存在");
        if (!question.getUserId().equals(userId)) return Result.fail("只能采纳自己的问题");
        question.setAcceptedAnswerId(body.get("answerId"));
        question.setStatus("resolved");
        question.setUpdateTime(LocalDateTime.now());
        questionService.updateById(question);
        return Result.ok();
    }
}
