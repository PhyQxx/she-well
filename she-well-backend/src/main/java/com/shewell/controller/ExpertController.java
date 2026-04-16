package com.shewell.controller;

import com.shewell.entity.*;
import com.shewell.service.*;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expert")
public class ExpertController {

    @Autowired private ExpertService expertService;
    @Autowired private ExpertQuestionService questionService;
    @Autowired private ExpertAnswerService answerService;

    @GetMapping("/list")
    public Result<List<Expert>> getExperts() {
        return Result.ok(expertService.lambdaQuery()
            .eq(Expert::getCertificationStatus, "approved")
            .eq(Expert::getStatus, 1)
            .orderByDesc(Expert::getRating).list());
    }

    @GetMapping("/{id}")
    public Result<Expert> getExpert(@PathVariable Long id) {
        return Result.ok(expertService.getById(id));
    }

    // ===== 专家提问 =====
    @PostMapping("/question")
    public Result<ExpertQuestion> ask(@RequestBody ExpertQuestion question, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        question.setUserId(userId);
        question.setStatus("pending");
        question.setViewCount(0);
        question.setAnswerCount(0);
        questionService.save(question);
        return Result.ok(question);
    }

    @GetMapping("/questions")
    public Result<List<ExpertQuestion>> getQuestions(
            @RequestParam(required = false) Long expertId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        var query = questionService.lambdaQuery()
            .eq(expertId != null, ExpertQuestion::getExpertId, expertId)
            .in(ExpertQuestion::getStatus, "pending", "answered")
            .orderByDesc(ExpertQuestion::getCreateTime);
        query.last("LIMIT " + (page - 1) * size + "," + size);
        return Result.ok(query.list());
    }

    @GetMapping("/question/{id}")
    public Result<ExpertQuestion> getQuestion(@PathVariable Long id) {
        return Result.ok(questionService.getById(id));
    }

    // ===== 专家回答 =====
    @PostMapping("/question/{id}/answer")
    public Result<ExpertAnswer> answer(@PathVariable Long id, @RequestBody ExpertAnswer answer, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        answer.setExpertId(userId);
        answer.setQuestionId(id);
        answer.setLikeCount(0);
        answer.setIsAdopted(0);
        answer.setStatus(1);
        answerService.save(answer);
        questionService.lambdaUpdate()
            .set(ExpertQuestion::getStatus, "answered")
            .setSql("answer_count = answer_count + 1")
            .eq(ExpertQuestion::getId, id).update();
        return Result.ok(answer);
    }

    @GetMapping("/question/{id}/answers")
    public Result<List<ExpertAnswer>> getAnswers(@PathVariable Long id) {
        return Result.ok(answerService.lambdaQuery()
            .eq(ExpertAnswer::getQuestionId, id)
            .eq(ExpertAnswer::getStatus, 1)
            .orderByDesc(ExpertAnswer::getCreateTime).list());
    }

    // ===== 管理端专家审批 =====
    @GetMapping("/all")
    public Result<List<Expert>> getAllExperts() {
        return Result.ok(expertService.lambdaQuery().orderByDesc(Expert::getCreateTime).list());
    }

    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        Expert expert = expertService.getById(id);
        if (expert == null) return Result.fail("专家不存在");
        expert.setCertificationStatus("approved");
        expert.setStatus(1);
        expert.setCertifyTime(java.time.LocalDateTime.now());
        expertService.updateById(expert);
        return Result.ok();
    }

    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        Expert expert = expertService.getById(id);
        if (expert == null) return Result.fail("专家不存在");
        expert.setCertificationStatus("rejected");
        expert.setRejectReason(body.get("reason"));
        expertService.updateById(expert);
        return Result.ok();
    }

    @DeleteMapping("/question/{id}")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        questionService.removeById(id);
        return Result.ok();
    }
}
