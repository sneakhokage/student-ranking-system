package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RiskStatsDTO {
    private Long studentId;
    private String semesterId;
    private Long debtCount;
    private Boolean atRisk;
}
