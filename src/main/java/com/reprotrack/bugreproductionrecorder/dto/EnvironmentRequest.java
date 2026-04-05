package com.reprotrack.bugreproductionrecorder.dto;

import lombok.Data;

@Data
public class EnvironmentRequest {
    private String name;
    private String description;
    private String baseUrl;
    private String browserName;
    private String browserVersion;
    private String osName;
    private String osVersion;
    private String deviceType;
}
