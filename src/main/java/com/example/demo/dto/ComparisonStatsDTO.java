package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ComparisonStatsDTO {
    private Long studentId;
    private String semesterId;
    private Double studentAverage;
    private Double groupAverage;
    private Double specialtyAverage;
    private Double streamAverage;
}
