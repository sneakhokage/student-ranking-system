package com.example.demo.controller;

import com.example.demo.dto.ComparisonStatsDTO;
import com.example.demo.service.ComparisonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comparison")
public class ComparisonController {

    private final ComparisonService comparisonService;

    public ComparisonController(ComparisonService comparisonService) {
        this.comparisonService = comparisonService;
    }

    @GetMapping("/student")
    public ComparisonStatsDTO getStudentComparison(
            @RequestParam Long studentId,
            @RequestParam(defaultValue = "SEM1") String semesterId
    ) {
        return comparisonService.getStudentComparison(studentId, semesterId);
    }
}
