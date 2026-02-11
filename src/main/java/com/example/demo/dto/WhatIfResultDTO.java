package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WhatIfResultDTO {
    private Long studentId;
    private String semesterId;
    private Double currentWeightedGpa;
    private Double projectedWeightedGpa;
    private Double delta;
}
