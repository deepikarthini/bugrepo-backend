package com.reprotrack.bugreproductionrecorder.service;

import com.reprotrack.bugreproductionrecorder.dto.BugAiInsightsResponse;
import com.reprotrack.bugreproductionrecorder.entity.BugAiAnalysis;
import com.reprotrack.bugreproductionrecorder.entity.BugReport;
import com.reprotrack.bugreproductionrecorder.entity.BugStep;
import com.reprotrack.bugreproductionrecorder.repository.BugAiAnalysisRepository;
import com.reprotrack.bugreproductionrecorder.repository.BugReportRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BugAiInsightService {

    private static final Set<String> STOP_WORDS = Set.of(
            "the", "and", "for", "with", "from", "that", "this", "when", "then", "into",
            "have", "has", "had", "was", "were", "are", "is", "but", "not", "you", "your",
            "after", "before", "click", "enter", "page", "screen", "button"
    );

    private final BugAiAnalysisRepository bugAiAnalysisRepository;
    private final BugReportRepository bugReportRepository;
    private final ObjectMapper objectMapper;

    public BugAiInsightService(BugAiAnalysisRepository bugAiAnalysisRepository,
                               BugReportRepository bugReportRepository,
                               ObjectMapper objectMapper) {
        this.bugAiAnalysisRepository = bugAiAnalysisRepository;
        this.bugReportRepository = bugReportRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public BugAiInsightsResponse getInsights(Long bugId) {
        return bugAiAnalysisRepository.findByBugReportId(bugId)
                .map(this::toResponse)
                .orElseGet(() -> regenerateInsights(bugId));
    }

    @Transactional
    public BugAiInsightsResponse regenerateInsights(Long bugId) {
        BugReport bug = bugReportRepository.findById(bugId)
                .orElseThrow(() -> new RuntimeException("Bug report not found"));

        BugAiInsightsResponse response = new BugAiInsightsResponse();
        response.setSummary(buildSummary(bug));
        response.setSuggestedPriority(suggestPriority(bug));
        response.setRootCauseHint(buildRootCauseHint(bug));
        response.setReasoning(buildReasoning(bug));
        response.setDuplicateCandidates(findDuplicateCandidates(bug));
        response.setPlaywrightScript(generatePlaywrightScript(bug));
        response.setExportMarkdown(buildExportMarkdown(bug, response));
        response.setGeneratedAt(LocalDateTime.now());

        BugAiAnalysis analysis = bugAiAnalysisRepository.findByBugReportId(bugId)
            .orElseGet(() -> BugAiAnalysis.builder().bugReport(bug).build());
        applyResponseToEntity(response, analysis);
        bugAiAnalysisRepository.save(analysis);
        return response;
    }

    private String buildSummary(BugReport bug) {
        String environment = bug.getEnvironment() != null ? bug.getEnvironment().getName() : "Unknown environment";
        int stepCount = bug.getSteps() != null ? bug.getSteps().size() : 0;
        String description = safeText(bug.getDescription());
        String trimmedDescription = description.length() > 180 ? description.substring(0, 177) + "..." : description;
        return String.format(
                "%s is reported in %s. The issue is currently %s with %d recorded reproduction step%s. %s",
                bug.getTitle(),
                environment,
                bug.getStatus().name(),
                stepCount,
                stepCount == 1 ? "" : "s",
                trimmedDescription
        );
    }

    private String suggestPriority(BugReport bug) {
        String content = collectAnalysisText(bug);
        if (containsAny(content, "payment", "checkout", "crash", "fatal", "data loss", "security", "login failed")) {
            return "Critical";
        }
        if (containsAny(content, "login", "submit", "save", "broken", "exception", "error", "cannot")) {
            return "High";
        }
        if (containsAny(content, "slow", "alignment", "layout", "display", "missing")) {
            return "Medium";
        }
        return "Low";
    }

    private String buildRootCauseHint(BugReport bug) {
        String content = collectAnalysisText(bug);
        if (containsAny(content, "login", "token", "auth", "unauthorized", "forbidden")) {
            return "Likely in the authentication or session handling flow. Check token validation, route guards, and backend auth filters.";
        }
        if (containsAny(content, "assign", "status", "save", "update", "submit")) {
            return "Likely in API request/response mapping or backend workflow state transitions. Verify payload shape and enum mapping.";
        }
        if (containsAny(content, "browser", "render", "style", "layout", "responsive")) {
            return "Likely in frontend rendering or browser-specific UI behavior. Check component state and CSS interactions.";
        }
        return "Start with the module directly tied to the first failing reproduction step and compare frontend payloads with backend expectations.";
    }

    private List<String> buildReasoning(BugReport bug) {
        List<String> reasoning = new ArrayList<>();
        reasoning.add("Suggested priority is based on keywords in the title, description, and reproduction steps.");
        if (bug.getEnvironment() != null) {
            reasoning.add("Environment context included: " + bug.getEnvironment().getName());
        }
        if (bug.getSteps() != null && !bug.getSteps().isEmpty()) {
            reasoning.add("Playwright script generation used " + bug.getSteps().size() + " persisted reproduction step(s).");
        } else {
            reasoning.add("No persisted reproduction steps were found, so script generation falls back to descriptive comments.");
        }
        return reasoning;
    }

    private List<BugAiInsightsResponse.DuplicateCandidate> findDuplicateCandidates(BugReport currentBug) {
        Set<String> currentTokens = tokenize(collectAnalysisText(currentBug));

        return bugReportRepository.findAllByOrderByIdAsc().stream()
                .filter(bug -> !bug.getId().equals(currentBug.getId()))
                .map(candidate -> toDuplicateCandidate(currentBug, currentTokens, candidate))
                .filter(candidate -> candidate.getSimilarityScore() >= 25)
                .sorted(Comparator.comparingInt(BugAiInsightsResponse.DuplicateCandidate::getSimilarityScore).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    private BugAiInsightsResponse.DuplicateCandidate toDuplicateCandidate(
            BugReport currentBug,
            Set<String> currentTokens,
            BugReport candidateBug
    ) {
        Set<String> candidateTokens = tokenize(collectAnalysisText(candidateBug));
        Set<String> overlap = new LinkedHashSet<>(currentTokens);
        overlap.retainAll(candidateTokens);

        int score = overlap.size() * 12;
        if (safeText(currentBug.getTitle()).equalsIgnoreCase(safeText(candidateBug.getTitle()))) {
            score += 30;
        }
        if (currentBug.getEnvironment() != null && candidateBug.getEnvironment() != null
                && safeText(currentBug.getEnvironment().getName()).equalsIgnoreCase(safeText(candidateBug.getEnvironment().getName()))) {
            score += 15;
        }

        BugAiInsightsResponse.DuplicateCandidate candidate = new BugAiInsightsResponse.DuplicateCandidate();
        candidate.setId(String.valueOf(candidateBug.getId()));
        candidate.setTitle(candidateBug.getTitle());
        candidate.setStatus(candidateBug.getStatus().name());
        candidate.setPriority(candidateBug.getSeverity().name());
        candidate.setSimilarityScore(Math.min(score, 95));
        candidate.setReason(overlap.isEmpty()
                ? "Similar workflow and environment signals were detected."
                : "Shared keywords: " + String.join(", ", overlap.stream().limit(5).collect(Collectors.toList())));
        return candidate;
    }

    private String generatePlaywrightScript(BugReport bug) {
        List<BugStep> steps = bug.getSteps() == null ? List.of() : bug.getSteps().stream()
                .sorted(Comparator.comparing(BugStep::getStepNumber))
                .collect(Collectors.toList());

        StringBuilder script = new StringBuilder();
        script.append("import { test, expect } from '@playwright/test';\n\n");
        script.append("test('").append(escapeJs(bug.getTitle())).append("', async ({ page }) => {\n");
        script.append("  // Generated from BugRepo reproduction steps\n");

        if (bug.getEnvironment() != null) {
            script.append("  // Environment: ").append(escapeJs(bug.getEnvironment().getName())).append("\n");
        }

        if (steps.isEmpty()) {
            script.append("  // No detailed steps were persisted for this bug yet.\n");
            script.append("  // Reproduction summary: ").append(escapeJs(safeText(bug.getDescription()))).append("\n");
        } else {
            for (BugStep step : steps) {
                script.append("\n  // Step ").append(step.getStepNumber()).append(": ")
                        .append(escapeJs(step.getDescription())).append("\n");
                switch (step.getActionType()) {
                    case NAVIGATE -> script.append("  await page.goto('https://example.com');\n");
                    case INPUT -> script.append("  await page.locator('input').fill('")
                            .append(escapeJs(safeText(step.getInputValue()))).append("');\n");
                    case WAIT -> script.append("  await page.waitForTimeout(")
                            .append(Math.max(step.getDuration(), 1000L)).append(");\n");
                    case SCROLL -> script.append("  await page.mouse.wheel(0, 800);\n");
                    default -> script.append("  await page.locator('body').click();\n");
                }
                if (step.getExpectedValue() != null && !step.getExpectedValue().isBlank()) {
                    script.append("  await expect(page.locator('body')).toContainText('")
                            .append(escapeJs(step.getExpectedValue())).append("');\n");
                }
            }
        }

        script.append("});\n");
        return script.toString();
    }

    private String buildExportMarkdown(BugReport bug, BugAiInsightsResponse response) {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# BugRepo AI Report\n\n");
        markdown.append("## Bug\n");
        markdown.append("- ID: ").append(bug.getId()).append("\n");
        markdown.append("- Title: ").append(bug.getTitle()).append("\n");
        markdown.append("- Status: ").append(bug.getStatus().name()).append("\n");
        markdown.append("- Reported Priority: ").append(bug.getSeverity().name()).append("\n");
        markdown.append("- Suggested Priority: ").append(response.getSuggestedPriority()).append("\n\n");
        markdown.append("## Summary\n").append(response.getSummary()).append("\n\n");
        markdown.append("## Root Cause Hint\n").append(response.getRootCauseHint()).append("\n\n");

        markdown.append("## Duplicate Candidates\n");
        if (response.getDuplicateCandidates().isEmpty()) {
            markdown.append("- No close duplicates found.\n");
        } else {
            for (BugAiInsightsResponse.DuplicateCandidate candidate : response.getDuplicateCandidates()) {
                markdown.append("- #").append(candidate.getId())
                        .append(" ").append(candidate.getTitle())
                        .append(" (").append(candidate.getSimilarityScore()).append("%) - ")
                        .append(candidate.getReason()).append("\n");
            }
        }

        markdown.append("\n## Playwright Script\n```javascript\n")
                .append(response.getPlaywrightScript())
                .append("```\n");

        return markdown.toString();
    }

    private BugAiInsightsResponse toResponse(BugAiAnalysis analysis) {
        BugAiInsightsResponse response = new BugAiInsightsResponse();
        response.setSummary(analysis.getSummary());
        response.setSuggestedPriority(analysis.getSuggestedPriority());
        response.setRootCauseHint(analysis.getRootCauseHint());
        response.setReasoning(readList(analysis.getReasoningJson(), new TypeReference<List<String>>() {}));
        response.setDuplicateCandidates(readList(
                analysis.getDuplicateCandidatesJson(),
                new TypeReference<List<BugAiInsightsResponse.DuplicateCandidate>>() {}
        ));
        response.setPlaywrightScript(analysis.getPlaywrightScript());
        response.setExportMarkdown(analysis.getExportMarkdown());
        response.setGeneratedAt(analysis.getGeneratedAt());
        return response;
    }

    private void applyResponseToEntity(BugAiInsightsResponse response, BugAiAnalysis analysis) {
        analysis.setSummary(response.getSummary());
        analysis.setSuggestedPriority(response.getSuggestedPriority());
        analysis.setRootCauseHint(response.getRootCauseHint());
        analysis.setReasoningJson(writeJson(response.getReasoning()));
        analysis.setDuplicateCandidatesJson(writeJson(response.getDuplicateCandidates()));
        analysis.setPlaywrightScript(response.getPlaywrightScript());
        analysis.setExportMarkdown(response.getExportMarkdown());
        analysis.setGeneratedAt(response.getGeneratedAt());
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value == null ? List.of() : value);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Failed to serialize AI analysis", exception);
        }
    }

    private <T> T readList(String json, TypeReference<T> typeReference) {
        if (json == null || json.isBlank()) {
            try {
                return objectMapper.readValue("[]", typeReference);
            } catch (JsonProcessingException exception) {
                throw new RuntimeException("Failed to deserialize empty AI analysis payload", exception);
            }
        }

        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Failed to deserialize AI analysis", exception);
        }
    }

    private String collectAnalysisText(BugReport bug) {
        StringBuilder text = new StringBuilder();
        text.append(safeText(bug.getTitle())).append(' ')
                .append(safeText(bug.getDescription())).append(' ');
        if (bug.getEnvironment() != null) {
            text.append(safeText(bug.getEnvironment().getName())).append(' ');
        }
        if (bug.getSteps() != null) {
            for (BugStep step : bug.getSteps()) {
                text.append(safeText(step.getDescription())).append(' ')
                        .append(safeText(step.getExpectedValue())).append(' ')
                        .append(safeText(step.getActualValue())).append(' ');
            }
        }
        return text.toString().toLowerCase(Locale.ENGLISH);
    }

    private Set<String> tokenize(String input) {
        return Arrays.stream(input.split("[^a-zA-Z0-9]+"))
                .map(String::trim)
                .map(token -> token.toLowerCase(Locale.ENGLISH))
                .filter(token -> token.length() > 2)
                .filter(token -> !STOP_WORDS.contains(token))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean containsAny(String input, String... keywords) {
        return Arrays.stream(keywords).anyMatch(input::contains);
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private String escapeJs(String value) {
        return safeText(value)
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\r", " ")
                .replace("\n", " ");
    }
}