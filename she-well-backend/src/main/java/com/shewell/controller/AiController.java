package com.shewell.controller;

import com.shewell.entity.Notification;
import com.shewell.service.NotificationService;
import com.shewell.service.ai.AiService;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final NotificationService notificationService;

    /**
     * AI 健康咨询
     */
    @PostMapping("/consult")
    public Result<AiConsultVO> consult(@RequestBody AiConsultDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String answer = aiService.consult(userId, dto.getQuestion(), dto.getContext());
        AiConsultVO vo = new AiConsultVO();
        vo.setAnswer(answer);
        vo.setModel(defaultProvider());
        return Result.ok(vo);
    }

    /**
     * 获取 AI 咨询历史
     */
    @GetMapping("/history")
    public Result<List<Notification>> getHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        var list = notificationService.lambdaQuery()
            .eq(Notification::getUserId, userId)
            .eq(Notification::getType, "ai_consult")
            .orderByDesc(Notification::getCreateTime)
            .last("LIMIT " + (page - 1) * size + "," + size)
            .list();
        return Result.ok(list);
    }

    /**
     * 获取每日健康建议（带缓存，24h）
     */
    @GetMapping("/advice")
    public Result<AiAdviceVO> getAdvice(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String advice = aiService.getHealthAdvice(userId);
        AiAdviceVO vo = new AiAdviceVO();
        vo.setAdvice(advice);
        vo.setGeneratedAt(LocalDateTime.now().toString());
        return Result.ok(vo);
    }

    /**
     * AI 试纸识别
     */
    @PostMapping("/recognize-strip")
    public Result<AiService.StripResult> recognizeStrip(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        try {
            String base64 = java.util.Base64.getEncoder().encodeToString(file.getBytes());
            AiService.StripResult result = aiService.recognizeStrip(base64);
            return Result.ok(result);
        } catch (Exception e) {
            return Result.fail("试纸识别失败：" + e.getMessage());
        }
    }

    /**
     * AI 周期分析报告
     */
    @GetMapping("/cycle-analysis")
    public Result<AiCycleAnalysisVO> getCycleAnalysis(
            @RequestParam(defaultValue = "3") int months,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String analysis = aiService.getCycleAnalysis(userId, months);
        AiCycleAnalysisVO vo = new AiCycleAnalysisVO();
        vo.setAnalysis(analysis);
        vo.setMonths(months);
        return Result.ok(vo);
    }

    /**
     * 获取可用 AI 模型列表
     */
    @GetMapping("/models")
    public Result<List<AiService.AiModel>> getModels() {
        return Result.ok(aiService.getAvailableModels());
    }

    private String defaultProvider() {
        return "deepseek";
    }

    // ===== DTO / VO =====

    @Data
    public static class AiConsultDTO {
        private String question;
        private String context;
    }

    @Data
    public static class AiConsultVO {
        private String answer;
        private String model;
    }

    @Data
    public static class AiAdviceVO {
        private String advice;
        private String generatedAt;
    }

    @Data
    public static class AiCycleAnalysisVO {
        private String analysis;
        private int months;
    }
}
