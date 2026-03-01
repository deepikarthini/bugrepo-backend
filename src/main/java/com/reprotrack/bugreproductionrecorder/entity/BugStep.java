package com.reprotrack.bugreproductionrecorder.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bug_steps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_report_id", nullable = false)
    private BugReport bugReport;

    @Column(nullable = false)
    private Integer stepNumber;

    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType; // CLICK, INPUT, SCROLL, NAVIGATE, WAIT, SCREENSHOT

    @Column(length = 500)
    private String elementSelector; // CSS selector or XPath for the element

    @Column(length = 500)
    private String elementName;

    @Column(length = 1000)
    private String inputValue;

    @Column(length = 1000)
    private String screenshotUrl;

    @Column(length = 1000)
    private String expectedValue;

    @Column(length = 1000)
    private String actualValue;

    @Column(nullable = false)
    private Long duration; // Duration in milliseconds

    private Double xCoordinate; // For click actions
    private Double yCoordinate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ActionType {
        CLICK, INPUT, SCROLL, NAVIGATE, WAIT, SCREENSHOT, HOVER, DOUBLE_CLICK,
        RIGHT_CLICK, KEY_PRESS, FORM_SUBMIT, PAGE_REFRESH, BACK_NAVIGATION
    }
}
