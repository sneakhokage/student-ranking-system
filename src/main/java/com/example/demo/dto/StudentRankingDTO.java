package com.example.demo.dto;

import com.example.demo.model.Students;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentRankingDTO {
    private Long studentId;
    private String fullName;
    private String groupName;
    private Double averageScore;
    private Long debtCount;
    private Students.FormOfStudy formOfStudy;
    private Long facultyId;
    private String facultyName;
}
