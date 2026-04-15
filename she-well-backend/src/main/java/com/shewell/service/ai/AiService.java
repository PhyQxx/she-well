package com.shewell.service.ai;

import com.shewell.entity.*;
import java.util.List;

public interface AiService {

    String consult(Long userId, String question, String context);

    String getHealthAdvice(Long userId);

    String getCycleAnalysis(Long userId, int months);

    StripResult recognizeStrip(String imageBase64);

    List<AiModel> getAvailableModels();

    @lombok.Data
    class StripResult {
        private String result;
        private Double lhValue;
        private Double confidence;
        private String advice;
    }

    @lombok.Data
@lombok.AllArgsConstructor
    class AiModel {
        private String code;
        private String name;
        private String defaultUrl;
    }
}
