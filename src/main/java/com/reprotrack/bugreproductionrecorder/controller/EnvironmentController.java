package com.reprotrack.bugreproductionrecorder.controller;

import com.reprotrack.bugreproductionrecorder.dto.EnvironmentRequest;
import com.reprotrack.bugreproductionrecorder.entity.Environment;
import com.reprotrack.bugreproductionrecorder.service.EnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/environments")
@CrossOrigin(origins = "*")
public class EnvironmentController {

    @Autowired
    private EnvironmentService environmentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Environment> createEnvironment(@RequestBody EnvironmentRequest request) {
        return ResponseEntity.ok(environmentService.createEnvironment(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('QA', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<List<Environment>> getAllEnvironments() {
        return ResponseEntity.ok(environmentService.getAllEnvironments());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('QA', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<List<Environment>> getActiveEnvironments() {
        return ResponseEntity.ok(environmentService.getActiveEnvironments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('QA', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<Environment> getEnvironmentById(@PathVariable Long id) {
        return ResponseEntity.ok(environmentService.getEnvironmentById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Environment> updateEnvironment(
            @PathVariable Long id,
            @RequestBody EnvironmentRequest request) {
        return ResponseEntity.ok(environmentService.updateEnvironment(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateEnvironment(@PathVariable Long id) {
        environmentService.deactivateEnvironment(id);
        return ResponseEntity.noContent().build();
    }
}
