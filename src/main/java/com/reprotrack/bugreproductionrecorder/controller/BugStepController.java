package com.reprotrack.bugreproductionrecorder.controller;

import com.reprotrack.bugreproductionrecorder.dto.BugStepRequest;
import com.reprotrack.bugreproductionrecorder.entity.BugStep;
import com.reprotrack.bugreproductionrecorder.service.BugStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bugs/{bugReportId}/steps")
@CrossOrigin(origins = "*")
public class BugStepController {

    @Autowired
    private BugStepService bugStepService;

    @PostMapping
    @PreAuthorize("hasAnyRole('QA', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<BugStep> addStep(
            @PathVariable Long bugReportId,
            @RequestBody BugStepRequest request) {
        return ResponseEntity.ok(bugStepService.addStep(bugReportId, request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('QA', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<List<BugStep>> getSteps(@PathVariable Long bugReportId) {
        return ResponseEntity.ok(bugStepService.getStepsByBugReport(bugReportId));
    }

    @DeleteMapping("/{stepId}")
    @PreAuthorize("hasAnyRole('QA', 'ADMIN')")
    public ResponseEntity<Void> deleteStep(@PathVariable Long stepId) {
        bugStepService.deleteStep(stepId);
        return ResponseEntity.noContent().build();
    }
}
