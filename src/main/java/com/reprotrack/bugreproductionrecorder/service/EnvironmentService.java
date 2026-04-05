package com.reprotrack.bugreproductionrecorder.service;

import com.reprotrack.bugreproductionrecorder.dto.EnvironmentRequest;
import com.reprotrack.bugreproductionrecorder.entity.Environment;
import com.reprotrack.bugreproductionrecorder.repository.EnvironmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnvironmentService {

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Transactional
    public Environment createEnvironment(EnvironmentRequest request) {
        Environment environment = Environment.builder()
                .name(request.getName())
                .description(request.getDescription())
                .baseUrl(request.getBaseUrl())
                .browserName(request.getBrowserName())
                .browserVersion(request.getBrowserVersion())
                .osName(request.getOsName())
                .osVersion(request.getOsVersion())
                .deviceType(request.getDeviceType())
                .isActive(true)
                .build();

        return environmentRepository.save(environment);
    }

    public List<Environment> getAllEnvironments() {
        return environmentRepository.findAll();
    }

    public List<Environment> getActiveEnvironments() {
        return environmentRepository.findByIsActiveTrue();
    }

    public Environment getEnvironmentById(Long id) {
        return environmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Environment not found"));
    }

    @Transactional
    public Environment updateEnvironment(Long id, EnvironmentRequest request) {
        Environment environment = getEnvironmentById(id);
        environment.setName(request.getName());
        environment.setDescription(request.getDescription());
        environment.setBaseUrl(request.getBaseUrl());
        environment.setBrowserName(request.getBrowserName());
        environment.setBrowserVersion(request.getBrowserVersion());
        environment.setOsName(request.getOsName());
        environment.setOsVersion(request.getOsVersion());
        environment.setDeviceType(request.getDeviceType());
        return environmentRepository.save(environment);
    }

    @Transactional
    public void deactivateEnvironment(Long id) {
        Environment environment = getEnvironmentById(id);
        environment.setIsActive(false);
        environmentRepository.save(environment);
    }
}
