package com.reprotrack.bugreproductionrecorder.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BugAiInsightsResponse {
    private String summary;
    private String suggestedPriority;
    private String rootCauseHint;
    private List<String> reasoning;
    private List<DuplicateCandidate> duplicateCandidates;
    private String playwrightScript;
    private String exportMarkdown;
    private LocalDateTime generatedAt;

    @Data
    public static class DuplicateCandidate {
        private String id;
        private String title;
        private String status;
        private String priority;
        private int similarityScore;
        private String reason;
    }
}