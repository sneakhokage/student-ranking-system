package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class GradeDTO {
    private Long id;
    private Long studentId;
    private Long subjectId;
    private String subjectName;
    private String semesterId;
    private Integer score;
    private Integer attempt;
    private LocalDate dateEntered;
}
