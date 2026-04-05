package com.reprotrack.bugreproductionrecorder.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "environments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Environment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Development, Staging, Production, QA

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String baseUrl;

    @Column(nullable = false)
    private String browserName; // Chrome, Firefox, Safari, Edge

    @Column(nullable = false)
    private String browserVersion;

    @Column(nullable = false)
    private String osName; // Windows, macOS, Linux

    @Column(nullable = false)
    private String osVersion;

    @Column(nullable = false)
    private String deviceType; // DESKTOP, TABLET, MOBILE

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
