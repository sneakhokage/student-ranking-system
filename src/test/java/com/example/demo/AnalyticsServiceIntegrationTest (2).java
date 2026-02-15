package com.example.demo;

import com.example.demo.dto.CorrelationDTO;
import com.example.demo.dto.RiskStatsDTO;
import com.example.demo.dto.TrendAnalysisDTO;
import com.example.demo.dto.WhatIfRequestDTO;
import com.example.demo.dto.WhatIfResultDTO;
import com.example.demo.dto.WhatIfScoreInputDTO;
import com.example.demo.dto.WeightedGpaDTO;
import com.example.demo.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AnalyticsServiceIntegrationTest {

    @Autowired
    private AnalyticsService analyticsService;

    @Test
    void weightedGpaIsInValidRange() {
        WeightedGpaDTO dto = analyticsService.calculateWeightedGpa(1L, "SEM1");
        assertEquals(1L, dto.getStudentId());
        assertEquals("SEM1", dto.getSemesterId());
        assertTrue(dto.getWeightedGpa() >= 0.0 && dto.getWeightedGpa() <= 100.0);
    }

    @Test
    void knownDebtorHasDebtInSem1() {
        RiskStatsDTO dto = analyticsService.calculateRisk(9L, "SEM1");
        assertEquals(9L, dto.getStudentId());
        assertEquals("SEM1", dto.getSemesterId());
        assertTrue(dto.getDebtCount() > 0);
        assertTrue(dto.getAtRisk());
    }

    @Test
    void trendAndCorrelationAreComputed() {
        TrendAnalysisDTO trend = analyticsService.calculateTrend(1L);
        CorrelationDTO correlation = analyticsService.calculateAttendancePerformanceCorrelation(1L, "SEM1");

        assertTrue(trend.getPoints() != null && !trend.getPoints().isEmpty());
        assertTrue(
                "UP".equals(trend.getTrendDirection())
                        || "DOWN".equals(trend.getTrendDirection())
                        || "STABLE".equals(trend.getTrendDirection())
        );
        assertTrue(correlation.getPearsonCorrelation() >= -1.0 && correlation.getPearsonCorrelation() <= 1.0);
        assertTrue(correlation.getSamples() >= 0);
    }

    @Test
    void whatIfDeltaMatchesProjectedMinusCurrent() {
        WhatIfRequestDTO request = new WhatIfRequestDTO(
                1L,
                "SEM1",
                List.of(
                        new WhatIfScoreInputDTO(1L, 100),
                        new WhatIfScoreInputDTO(2L, 100)
                )
        );

        WhatIfResultDTO dto = analyticsService.calculateWhatIf(request);
        double expectedDelta = dto.getProjectedWeightedGpa() - dto.getCurrentWeightedGpa();
        assertTrue(Math.abs(expectedDelta - dto.getDelta()) < 0.000001);
    }
}
