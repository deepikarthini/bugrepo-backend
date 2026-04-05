package com.reprotrack.bugreproductionrecorder.controller;

import com.reprotrack.bugreproductionrecorder.dto.BugReportRequest;
import com.reprotrack.bugreproductionrecorder.dto.BugReportResponse;
import com.reprotrack.bugreproductionrecorder.entity.BugReport;
import com.reprotrack.bugreproductionrecorder.service.BugReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bugs")
@CrossOrigin(origins = "*")
public class BugReportController {

    @Autowired
    private BugReportService bugReportService;

    @PostMapping
    @PreAuthorize("hasAnyRole('QA', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<BugReportResponse> createBugReport(@RequestBody BugReportRequest request) {
        return ResponseEntity.ok(bugReportService.createBugReport(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('QA', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<List<BugReportResponse>> getAllBugReports() {
        return ResponseEntity.ok(bugReportService.getAllBugReports());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('QA', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<BugReportResponse> getBugReportById(@PathVariable Long id) {
        return ResponseEntity.ok(bugReportService.getBugReportById(id));
    }

    @GetMapping("/my-reports")
    @PreAuthorize("hasAnyRole('QA', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<List<BugReportResponse>> getMyReportedBugs() {
        return ResponseEntity.ok(bugReportService.getMyReportedBugs());
    }

    @GetMapping("/my-assigned")
    @PreAuthorize("hasRole('DEVELOPER')")
    public ResponseEntity<List<BugReportResponse>> getMyAssignedBugs() {
        return ResponseEntity.ok(bugReportService.getMyAssignedBugs());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DEVELOPER', 'ADMIN')")
    public ResponseEntity<BugReportResponse> updateBugStatus(
            @PathVariable Long id,
            @RequestParam BugReport.BugStatus status) {
        return ResponseEntity.ok(bugReportService.updateBugStatus(id, status));
    }

    @PatchMapping("/{bugId}/assign/{developerId}")
    @PreAuthorize("hasAnyRole('QA', 'ADMIN')")
    public ResponseEntity<BugReportResponse> assignBug(
            @PathVariable Long bugId,
            @PathVariable Long developerId) {
        return ResponseEntity.ok(bugReportService.assignBug(bugId, developerId));
    }
}
