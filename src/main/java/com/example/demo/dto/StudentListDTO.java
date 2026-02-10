package com.example.demo.dto;

import com.example.demo.model.Students;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentListDTO {
    private Long id;
    private String fullName;
    private String groupName;
    private Students.FormOfStudy formOfStudy;
    private Students.StudentStatus status;
}
