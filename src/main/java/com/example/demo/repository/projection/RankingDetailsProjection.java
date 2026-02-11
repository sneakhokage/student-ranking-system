package com.example.demo.repository.projection;

import com.example.demo.model.Students;

public interface RankingDetailsProjection {
    Long getStudentId();
    String getFullName();
    String getGroupName();
    Double getAverageScore();
    Long getDebtCount();
    Students.FormOfStudy getFormOfStudy();
    Long getFacultyId();
    String getFacultyName();
}
