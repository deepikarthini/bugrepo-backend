package com.reprotrack.bugreproductionrecorder.repository;

import com.reprotrack.bugreproductionrecorder.entity.BugAiAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BugAiAnalysisRepository extends JpaRepository<BugAiAnalysis, Long> {
    Optional<BugAiAnalysis> findByBugReportId(Long bugReportId);
}