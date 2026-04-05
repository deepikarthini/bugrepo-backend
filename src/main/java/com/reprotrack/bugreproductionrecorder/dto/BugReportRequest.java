package com.reprotrack.bugreproductionrecorder.dto;

import com.reprotrack.bugreproductionrecorder.entity.BugReport;
import lombok.Data;
import java.util.List;

@Data
public class BugReportRequest {
    private String title;
    private String description;
    private String priority; // Changed from severity to priority
    private String assignedTo; // Username string
    private String environment; // Environment string
    private List<ReproductionStep> reproductionSteps;
    
    @Data
    public static class ReproductionStep {
        private Integer step;
        private String description;
        private String screenshot;
        private String expected;
        private String actual;
    }
}
