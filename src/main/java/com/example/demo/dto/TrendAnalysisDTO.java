package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TrendAnalysisDTO {
    private Long studentId;
    private List<TrendPointDTO> points;
    private Double trendCoefficient;
    private String trendDirection;
}
