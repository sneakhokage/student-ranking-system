package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttendanceDTO {
    private Long id;
    private Long studentId;
    private Long subjectId;
    private String semesterId;
    private Integer percentage;
}
