package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentRankingDTO {
    private String fullName;
    private String groupName;
    private Double averageScore; //analytics dev (Vlad or Myroslav??)
}