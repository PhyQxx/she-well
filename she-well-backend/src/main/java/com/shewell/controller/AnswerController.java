package com.shewell.controller;

import com.shewell.entity.CommunityAnswer;
import com.shewell.entity.CommunityQuestion;
import com.shewell.entity.Expert;
import com.shewell.service.CommunityAnswerService;
import com.shewell.service.CommunityQuestionService;
import com.shewell.service.ExpertService;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 姐妹问答 - 回答
 * PRD: 3.9.3 问答互助
 */
@RestController
@RequestMapping("/api/community/answer")
@RequiredArgsConstructor
public class AnswerController {

    private final CommunityAnswerService answerService;
    private final CommunityQuestionService questionService;
    private final ExpertService expertService;

    @PostMapping
    public Result<CommunityAnswer> create(@RequestBody CommunityAnswer answer, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        answer.setUserId(userId);
        answer.setLikeCount(0);
        answer.setIsAdopted(false);
        answer.setStatus("published");
        answer.setCreateTime(LocalDateTime.now());
        answerService.save(answer);
        // 更新问题回答数
        CommunityQuestion question = questionService.getById(answer.getQuestionId());
        if (question != null) {
            question.setAnswerCount(question.getAnswerCount() == null ? 1 : question.getAnswerCount() + 1);
            questionService.updateById(question);
        }
        return Result.ok(answer);
    }

    @GetMapping("/list")
    public Result<?> list(
            @RequestParam Long questionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        List<CommunityAnswer> all = answerService.lambdaQuery()
            .eq(CommunityAnswer::getQuestionId, questionId)
            .orderByDesc(CommunityAnswer::getIsAdopted)
            .orderByDesc(CommunityAnswer::getLikeCount)
            .orderByAsc(CommunityAnswer::getCreateTime)
            .list();
        int total = all.size();
        int from = (page - 1) * size;
        List<CommunityAnswer> paged = all.subList(Math.min(from, total), Math.min(from + size, total));
        List<Map<String, Object>> list = paged.stream().map(a -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", a.getId());
            item.put("questionId", a.getQuestionId());
            item.put("content", a.getContent());
            item.put("likeCount", a.getLikeCount());
            item.put("isAccepted", a.getIsAdopted());
            item.put("createTime", a.getCreateTime());
            return item;
        }).toList();
        return Result.ok(Map.of("list", list, "total", total));
    }

    @PostMapping("/{id}/like")
    public Result<Void> like(@PathVariable Long id) {
        CommunityAnswer answer = answerService.getById(id);
        if (answer != null) {
            answer.setLikeCount(answer.getLikeCount() == null ? 1 : answer.getLikeCount() + 1);
            answerService.updateById(answer);
        }
        return Result.ok();
    }
}
