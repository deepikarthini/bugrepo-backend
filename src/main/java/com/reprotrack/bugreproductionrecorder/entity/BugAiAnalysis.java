package com.reprotrack.bugreproductionrecorder.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bug_ai_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugAiAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_report_id", nullable = false, unique = true)
    private BugReport bugReport;

    @Column(length = 3000)
    private String summary;

    @Column(length = 50)
    private String suggestedPriority;

    @Column(length = 3000)
    private String rootCauseHint;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String reasoningJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String duplicateCandidatesJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String playwrightScript;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String exportMarkdown;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}