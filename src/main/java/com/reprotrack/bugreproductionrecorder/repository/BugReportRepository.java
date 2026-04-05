package com.reprotrack.bugreproductionrecorder.repository;

import com.reprotrack.bugreproductionrecorder.entity.BugReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugReportRepository extends JpaRepository<BugReport, Long> {
    List<BugReport> findByReportedByIdOrderByIdAsc(Long userId);
    List<BugReport> findByAssignedToIdOrderByIdAsc(Long userId);
    List<BugReport> findByStatusOrderByIdAsc(BugReport.BugStatus status);
    List<BugReport> findBySeverityOrderByIdAsc(BugReport.BugSeverity severity);
    List<BugReport> findAllByOrderByIdAsc();
}
