package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CorrelationDTO {
    private Long studentId;
    private String semesterId;
    private Integer samples;
    private Double pearsonCorrelation;
}
