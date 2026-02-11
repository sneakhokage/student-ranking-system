package com.example.demo.controller;

import com.example.demo.dto.CorrelationDTO;
import com.example.demo.dto.RiskStatsDTO;
import com.example.demo.dto.TrendAnalysisDTO;
import com.example.demo.dto.WeightedGpaDTO;
import com.example.demo.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/weighted-gpa")
    public WeightedGpaDTO getWeightedGpa(
            @RequestParam Long studentId,
            @RequestParam(defaultValue = "SEM1") String semesterId
    ) {
        return analyticsService.calculateWeightedGpa(studentId, semesterId);
    }

    @GetMapping("/risk")
    public RiskStatsDTO getRisk(
            @RequestParam Long studentId,
            @RequestParam(defaultValue = "SEM1") String semesterId
    ) {
        return analyticsService.calculateRisk(studentId, semesterId);
    }

    @GetMapping("/trend")
    public TrendAnalysisDTO getTrend(@RequestParam Long studentId) {
        return analyticsService.calculateTrend(studentId);
    }

    @GetMapping("/correlation")
    public CorrelationDTO getCorrelation(
            @RequestParam Long studentId,
            @RequestParam(defaultValue = "SEM1") String semesterId
    ) {
        return analyticsService.calculateAttendancePerformanceCorrelation(studentId, semesterId);
    }
}
