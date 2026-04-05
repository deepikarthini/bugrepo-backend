package com.reprotrack.bugreproductionrecorder.repository;

import com.reprotrack.bugreproductionrecorder.entity.BugAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugAssignmentRepository extends JpaRepository<BugAssignment, Long> {
    List<BugAssignment> findByBugReportId(Long bugReportId);
    List<BugAssignment> findByDeveloperId(Long developerId);
    List<BugAssignment> findByStatus(BugAssignment.AssignmentStatus status);
}
