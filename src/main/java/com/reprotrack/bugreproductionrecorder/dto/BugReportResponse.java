package com.reprotrack.bugreproductionrecorder.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BugReportResponse {
    private String id; // Changed to String to match frontend
    private String title;
    private String description;
    private String priority; // Changed from severity
    private String status; // Changed to String
    private String assignedTo; // Changed from assignedToName
    private String environment; // Changed from environmentName
    private List<ReproductionStep> reproductionSteps;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    public static class ReproductionStep {
        private Integer step;
        private String description;
        private String screenshot;
        private String expected;
        private String actual;
    }
}
