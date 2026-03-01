package com.reprotrack.bugreproductionrecorder.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bug_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BugSeverity severity; // CRITICAL, HIGH, MEDIUM, LOW

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BugStatus status; // NEW, IN_PROGRESS, ASSIGNED, RESOLVED, CLOSED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_id", nullable = false)
    private User reportedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "environment_id", nullable = false)
    private Environment environment;

    @OneToMany(mappedBy = "bugReport", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BugStep> steps;

    @Column(length = 500)
    private String expectedResult;

    @Column(length = 500)
    private String actualResult;

    @Column(length = 1000)
    private String screenshotUrl;

    @Column(length = 1000)
    private String videoUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    private LocalDateTime resolvedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum BugSeverity {
        CRITICAL, HIGH, MEDIUM, LOW
    }

    public enum BugStatus {
        NEW, IN_PROGRESS, ASSIGNED, RESOLVED, CLOSED
    }
}
