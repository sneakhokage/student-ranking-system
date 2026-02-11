package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeightedGpaDTO {
    private Long studentId;
    private String semesterId;
    private Double weightedGpa;
}
