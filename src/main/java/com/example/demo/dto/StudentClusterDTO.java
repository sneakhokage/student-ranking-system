package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentClusterDTO {
    private Long studentId;
    private String fullName;
    private String semesterId;
    private Double averageScore;
    private Long debtCount;
    private String cluster;
}
