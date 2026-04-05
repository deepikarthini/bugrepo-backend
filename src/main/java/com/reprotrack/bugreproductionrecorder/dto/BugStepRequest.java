package com.reprotrack.bugreproductionrecorder.dto;

import com.reprotrack.bugreproductionrecorder.entity.BugStep;
import lombok.Data;

@Data
public class BugStepRequest {
    private Integer stepNumber;
    private String description;
    private BugStep.ActionType actionType;
    private String elementSelector;
    private String elementName;
    private String inputValue;
    private String screenshotUrl;
    private String expectedValue;
    private String actualValue;
    private Long duration;
    private Double xCoordinate;
    private Double yCoordinate;
}
