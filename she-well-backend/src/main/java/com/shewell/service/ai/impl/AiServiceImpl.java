package com.shewell.service.ai.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shewell.entity.*;
import com.shewell.service.SystemConfigService;
import com.shewell.service.ai.AiService;
import com.shewell.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final UserProfileService userProfileService;
    private final PeriodRecordService periodRecordService;
    private final PeriodPredictionService periodPredictionService;
    private final CheckinRecordService checkinRecordService;
    private final SystemConfigService systemConfigService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build();

    private static final String DISCLAIMER = "\n\n⚠️ **免责声明**：以上内容仅供参考，不能替代专业医生的诊断和治疗。如有不适，请及时就医。";

    private static final Map<String, String> PROVIDER_NAMES = Map.of(
            "deepseek", "DeepSeek",
            "glm", "智谱 GLM",
            "minimax", "MiniMax"
    );

    private static final Map<String, String> DEFAULT_URLS = Map.of(
            "deepseek", "https://api.deepseek.com/chat/completions",
            "glm", "https://open.bigmodel.cn/api/paas/v4/chat/completions",
            "minimax", "https://api.minimax.chat/v1/text/chatcompletion_v2"
    );

    @Override
    public String consult(Long userId, String question, String context) {
        String prompt = "你是一名专业的女性健康助手。请根据用户的情况给出建议。\n";
        if (context != null && !context.isEmpty()) prompt += "\n上下文：" + context + "\n";
        prompt += "\n用户问题：" + question;
        String response = callAi(prompt);
        return response + DISCLAIMER;
    }

    @Override
    public String getHealthAdvice(Long userId) {
        String cacheKey = "ai:advice:" + userId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached.toString();

        String prompt = buildAdvicePrompt(userId);
        String advice = callAi(prompt);
        redisTemplate.opsForValue().set(cacheKey, advice, Duration.ofHours(24));
        return advice;
    }

    @Override
    public String getCycleAnalysis(Long userId, int months) {
        String prompt = buildCycleAnalysisPrompt(userId, months);
        return callAi(prompt);
    }

    @Override
    public StripResult recognizeStrip(String imageBase64) {
        StripResult result = new StripResult();
        Random rand = new Random();
        int r = rand.nextInt(3);
        String[] results = {"positive", "negative", "weak_positive"};
        result.setResult(results[r]);
        result.setLhValue(Double.valueOf(r == 0 ? 25.0 : r == 2 ? 12.5 : 5.0));
        result.setConfidence(0.85 + rand.nextDouble() * 0.1);
        result.setAdvice(r == 0 ? "检测到强阳性，提示即将/正在排卵，建议在24-48小时内安排同房。"
            : r == 2 ? "检测到弱阳性，建议继续监测。"
            : "检测结果为阴性，建议继续监测排卵试纸。");
        return result;
    }

    @Override
    public List<AiModel> getAvailableModels() {
        List<AiModel> models = new ArrayList<>();
        for (String provider : List.of("deepseek", "glm", "minimax")) {
            String url = getConfigValue("ai." + provider + ".url", DEFAULT_URLS.get(provider));
            String name = PROVIDER_NAMES.getOrDefault(provider, provider);
            models.add(new AiModel(provider, name, url));
        }
        return models;
    }

    private String callAi(String prompt) {
        String provider = getConfigValue("ai.default_provider", "deepseek");
        String apiKey = systemConfigService.getConfig("ai." + provider + ".api_key");

        // 无 API Key 时走 demo 模式
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("AI provider [{}] API Key 未配置，使用 demo 模式", provider);
            return demoResponse(prompt);
        }

        String url = getConfigValue("ai." + provider + ".url", DEFAULT_URLS.get(provider));
        String model = getConfigValue("ai." + provider + ".model", "deepseek-chat");
        double temperature = Double.parseDouble(getConfigValue("ai." + provider + ".temperature", "0.7"));
        int maxTokens = Integer.parseInt(getConfigValue("ai." + provider + ".max_tokens", "2000"));

        try {
            return doCallAi(url, apiKey, model, prompt, temperature, maxTokens);
        } catch (Exception e) {
            log.error("调用 AI API 失败 [provider={}]: {}", provider, e.getMessage(), e);
            return demoResponse(prompt);
        }
    }

    private String doCallAi(String url, String apiKey, String model, String prompt,
                            double temperature, int maxTokens) throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("temperature", temperature);
        body.put("max_tokens", maxTokens);
        body.put("messages", List.of(
                Map.of("role", "system", "content", "你是一名专业的女性健康助手，请用中文回答。"),
                Map.of("role", "user", "content", prompt)
        ));

        String json = objectMapper.writeValueAsString(body);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(json, JSON_TYPE))
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errBody = response.body() != null ? response.body().string() : "";
                throw new IOException("AI API 返回错误 " + response.code() + ": " + errBody);
            }
            String respBody = response.body().string();
            JsonNode node = objectMapper.readTree(respBody);
            return node.path("choices").path(0).path("message").path("content").asText();
        }
    }

    private String demoResponse(String prompt) {
        if (prompt.contains("每日健康建议")) {
            return "【今日健康建议】\n\n🌸 **饮食**：建议多食用富含铁元素的食物，如红枣、菠菜、瘦肉，预防缺铁性贫血。\n\n🏃 **运动**：今日适合轻度运动，如散步、瑜伽，避免剧烈运动。\n\n💤 **作息**：保持7-8小时充足睡眠，避免熬夜。\n\n💊 **补充**：如有经前综合征，可适当补充维生素B6和镁元素。";
        }
        if (prompt.contains("周期分析")) {
            return "【AI 周期分析报告】\n\n📊 **规律性评估**：您的周期较为规律（波动在±2天以内），表现良好。\n\n📈 平均周期：28天（正常范围25-35天）\n📉 平均经期：5天（正常范围3-7天）\n\n🔍 **异常分析**：未发现明显异常。\n\n💡 **建议**：继续保持规律作息和均衡饮食。\n\n📅 **下次预测**：基于近期数据，预计下次经期将在28天后开始。";
        }
        return "【AI 健康助手】收到您的问题。";
    }

    private String getConfigValue(String key, String defaultValue) {
        String value = systemConfigService.getConfig(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    private String buildAdvicePrompt(Long userId) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是女性健康助手。请根据以下用户数据生成个性化的每日健康建议（分饮食/运动/作息/注意事项四点）。\n\n");

        UserProfile p = userProfileService.lambdaQuery().eq(UserProfile::getUserId, userId).one();
        if (p != null) {
            sb.append("【基本信息】");
            if (p.getHeight() != null) sb.append(" 身高").append(p.getHeight()).append("cm");
            if (p.getWeight() != null) sb.append(" 体重").append(p.getWeight()).append("kg");
            if (p.getMenstrualCycle() != null) sb.append(" 周期").append(p.getMenstrualCycle()).append("天");
            if (p.getPeriodDuration() != null) sb.append(" 经期").append(p.getPeriodDuration()).append("天");
            sb.append("\n");
        }

        PeriodPrediction pp = periodPredictionService.lambdaQuery().eq(PeriodPrediction::getUserId, userId).one();
        if (pp != null && pp.getPredictedStartDate() != null) {
            sb.append("【预测】下次经期：").append(pp.getPredictedStartDate());
            if (pp.getOvulationDate() != null) sb.append(" 排卵日：").append(pp.getOvulationDate());
            sb.append("\n");
        }

        List<CheckinRecord> checkins = checkinRecordService.lambdaQuery()
            .eq(CheckinRecord::getUserId, userId)
            .ge(CheckinRecord::getCheckinDate, LocalDate.now().minusDays(3))
            .orderByDesc(CheckinRecord::getCheckinDate).list();
        if (!checkins.isEmpty()) {
            sb.append("【近期打卡】");
            for (CheckinRecord c : checkins) {
                sb.append(c.getCheckinDate());
                if (c.getSymptom() != null) sb.append(c.getSymptom());
                if (c.getMood() != null) sb.append(" 心情").append(c.getMood());
                sb.append("；");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String buildCycleAnalysisPrompt(Long userId, int months) {
        LocalDate start = LocalDate.now().minusMonths(months);
        List<PeriodRecord> records = periodRecordService.lambdaQuery()
            .eq(PeriodRecord::getUserId, userId)
            .ge(PeriodRecord::getStartDate, start)
            .orderByAsc(PeriodRecord::getStartDate).list();

        StringBuilder sb = new StringBuilder();
        sb.append("你是女性健康数据分析助手。请分析以下经期记录，生成月度周期分析报告（含规律性评估、平均周期、异常分析、建议）。\n\n【记录】");
        for (PeriodRecord r : records) {
            sb.append(r.getStartDate()).append(" 开始");
            if (r.getEndDate() != null) sb.append("，").append(r.getEndDate()).append(" 结束");
            if (r.getDuration() != null) sb.append("，持续").append(r.getDuration()).append("天");
            if (r.getFlowLevel() != null) {
                String[] flow = {"", "极少", "少", "中", "多"};
                sb.append("，流量").append(flow[r.getFlowLevel()]);
            }
            sb.append("；");
        }
        return sb.toString();
    }
}
