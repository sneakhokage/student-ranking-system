package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubjectDTO {
    private Long id;
    private String name;
    private String category;
    private int ects;
}
