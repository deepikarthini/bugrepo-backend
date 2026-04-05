package com.reprotrack.bugreproductionrecorder.repository;

import com.reprotrack.bugreproductionrecorder.entity.BugStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugStepRepository extends JpaRepository<BugStep, Long> {
    List<BugStep> findByBugReportIdOrderByStepNumberAsc(Long bugReportId);
}
