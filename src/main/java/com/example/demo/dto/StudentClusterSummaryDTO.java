package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StudentClusterSummaryDTO {
    private String semesterId;
    private Integer totalStudents;
    private Integer topCount;
    private Integer middleCount;
    private Integer riskCount;
    private List<StudentClusterDTO> students;
}
