package com.reprotrack.bugreproductionrecorder.service;

import com.reprotrack.bugreproductionrecorder.dto.BugStepRequest;
import com.reprotrack.bugreproductionrecorder.entity.BugReport;
import com.reprotrack.bugreproductionrecorder.entity.BugStep;
import com.reprotrack.bugreproductionrecorder.repository.BugReportRepository;
import com.reprotrack.bugreproductionrecorder.repository.BugStepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BugStepService {

    @Autowired
    private BugStepRepository bugStepRepository;

    @Autowired
    private BugReportRepository bugReportRepository;

    @Transactional
    public BugStep addStep(Long bugReportId, BugStepRequest request) {
        BugReport bugReport = bugReportRepository.findById(bugReportId)
                .orElseThrow(() -> new RuntimeException("Bug report not found"));

        BugStep step = BugStep.builder()
                .bugReport(bugReport)
                .stepNumber(request.getStepNumber())
                .description(request.getDescription())
                .actionType(request.getActionType())
                .elementSelector(request.getElementSelector())
                .elementName(request.getElementName())
                .inputValue(request.getInputValue())
                .screenshotUrl(request.getScreenshotUrl())
                .expectedValue(request.getExpectedValue())
                .actualValue(request.getActualValue())
                .duration(request.getDuration())
                .xCoordinate(request.getXCoordinate())
                .yCoordinate(request.getYCoordinate())
                .build();

        return bugStepRepository.save(step);
    }

    public List<BugStep> getStepsByBugReport(Long bugReportId) {
        return bugStepRepository.findByBugReportIdOrderByStepNumberAsc(bugReportId);
    }

    @Transactional
    public void deleteStep(Long stepId) {
        bugStepRepository.deleteById(stepId);
    }
}
