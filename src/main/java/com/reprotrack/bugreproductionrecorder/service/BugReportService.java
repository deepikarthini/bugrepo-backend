package com.reprotrack.bugreproductionrecorder.service;

import com.reprotrack.bugreproductionrecorder.dto.BugReportRequest;
import com.reprotrack.bugreproductionrecorder.dto.BugReportResponse;
import com.reprotrack.bugreproductionrecorder.entity.BugReport;
import com.reprotrack.bugreproductionrecorder.entity.Environment;
import com.reprotrack.bugreproductionrecorder.entity.User;
import com.reprotrack.bugreproductionrecorder.repository.BugReportRepository;
import com.reprotrack.bugreproductionrecorder.repository.EnvironmentRepository;
import com.reprotrack.bugreproductionrecorder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BugReportService {

    @Autowired
    private BugReportRepository bugReportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Transactional
    public BugReportResponse createBugReport(BugReportRequest request) {
        // For testing without authentication - use a default user or create anonymous
        User reportedBy = null;
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            if (email != null && !email.equals("anonymousUser")) {
                reportedBy = userRepository.findByEmail(email).orElse(null);
            }
        } catch (Exception e) {
            // No authentication, continue without user
        }
        
        // If no authenticated user, create/get a system user
        if (reportedBy == null) {
            reportedBy = userRepository.findByEmail("system@bugreporter.com")
                    .orElseGet(() -> {
                        User systemUser = User.builder()
                                .email("system@bugreporter.com")
                                .password("$2a$10$dummyhashedpassword") // Dummy password
                                .fullName("System User")
                                .role(User.UserRole.QA)
                                .isActive(true)
                                .build();
                        return userRepository.save(systemUser);
                    });
        }

        // Parse environment string (e.g., "Windows / Chrome / v120.0")
        String envString = request.getEnvironment() != null ? request.getEnvironment() : "Unknown / Unknown / Unknown";
        String[] envParts = envString.split(" / ");
        String osName = envParts.length > 0 ? envParts[0].trim() : "Unknown";
        String browserName = envParts.length > 1 ? envParts[1].trim() : "Unknown";
        String version = envParts.length > 2 ? envParts[2].trim() : "Unknown";

        // Find or create environment
        Environment environment = environmentRepository.findByName(envString)
                .orElseGet(() -> {
                    Environment env = Environment.builder()
                            .name(envString)
                            .description("Auto-created from bug report")
                            .baseUrl("http://localhost")
                            .browserName(browserName)
                            .browserVersion(version)
                            .osName(osName)
                            .osVersion("Unknown")
                            .deviceType("DESKTOP")
                            .isActive(true)
                            .build();
                    return environmentRepository.save(env);
                });

        // Map priority string to severity enum
        BugReport.BugSeverity severity;
        try {
            severity = BugReport.BugSeverity.valueOf(request.getPriority().toUpperCase());
        } catch (Exception e) {
            severity = BugReport.BugSeverity.MEDIUM;
        }

        // Find assignedTo user if provided
        User assignedToUser = null;
        if (request.getAssignedTo() != null && !request.getAssignedTo().isEmpty()) {
            assignedToUser = userRepository.findByEmail(request.getAssignedTo()).orElse(null);
        }

        BugReport bugReport = BugReport.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .severity(severity)
                .status(BugReport.BugStatus.NEW)
                .reportedBy(reportedBy)
                .assignedTo(assignedToUser)
                .environment(environment)
                .expectedResult(request.getReproductionSteps() != null && !request.getReproductionSteps().isEmpty() 
                        ? request.getReproductionSteps().get(0).getExpected() : null)
                .actualResult(request.getReproductionSteps() != null && !request.getReproductionSteps().isEmpty()
                        ? request.getReproductionSteps().get(0).getActual() : null)
                .build();

        bugReport = bugReportRepository.save(bugReport);
        return mapToResponse(bugReport);
    }

    @Transactional
    public List<BugReportResponse> getAllBugReports() {
        return bugReportRepository.findAllByOrderByIdAsc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BugReportResponse getBugReportById(Long id) {
        BugReport bugReport = bugReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bug report not found"));
        return mapToResponse(bugReport);
    }

    @Transactional
    public BugReportResponse updateBugStatus(Long id, BugReport.BugStatus status) {
        BugReport bugReport = bugReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bug report not found"));
        bugReport.setStatus(status);
        bugReport = bugReportRepository.save(bugReport);
        return mapToResponse(bugReport);
    }

    @Transactional
    public BugReportResponse assignBug(Long bugId, Long developerId) {
        BugReport bugReport = bugReportRepository.findById(bugId)
                .orElseThrow(() -> new RuntimeException("Bug report not found"));
        User developer = userRepository.findById(developerId)
                .orElseThrow(() -> new RuntimeException("Developer not found"));

        bugReport.setAssignedTo(developer);
        bugReport.setStatus(BugReport.BugStatus.ASSIGNED);
        bugReport = bugReportRepository.save(bugReport);
        return mapToResponse(bugReport);
    }

    @Transactional
    public List<BugReportResponse> getMyReportedBugs() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bugReportRepository.findByReportedByIdOrderByIdAsc(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BugReportResponse> getMyAssignedBugs() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bugReportRepository.findByAssignedToIdOrderByIdAsc(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BugReportResponse mapToResponse(BugReport bugReport) {
        BugReportResponse response = new BugReportResponse();
        response.setId(String.valueOf(bugReport.getId()));
        response.setTitle(bugReport.getTitle());
        response.setDescription(bugReport.getDescription());
        
        // Map severity to priority (HIGH -> High, MEDIUM -> Medium, LOW -> Low)
        String priority = bugReport.getSeverity().name().charAt(0) + 
                         bugReport.getSeverity().name().substring(1).toLowerCase();
        response.setPriority(priority);
        
        // Map status (NEW -> Open, IN_PROGRESS -> In Progress, CLOSED -> Closed, etc.)
        String status;
        switch (bugReport.getStatus()) {
            case NEW:
            case ASSIGNED:
                status = "Open";
                break;
            case IN_PROGRESS:
                status = "In Progress";
                break;
            case CLOSED:
            case RESOLVED:
                status = "Closed";
                break;
            default:
                status = "Open";
        }
        response.setStatus(status);
        
        // Return email instead of fullName for proper frontend matching
        response.setAssignedTo(bugReport.getAssignedTo() != null ? 
                bugReport.getAssignedTo().getEmail() : null);
        response.setEnvironment(bugReport.getEnvironment() != null ? 
                bugReport.getEnvironment().getName() : null);
        response.setReproductionSteps(null); // TODO: Map bug steps if needed
        response.setCreatedAt(bugReport.getCreatedAt());
        response.setUpdatedAt(bugReport.getUpdatedAt());
        return response;
    }
}
