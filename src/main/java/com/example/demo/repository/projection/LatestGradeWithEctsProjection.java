package com.example.demo.repository.projection;

public interface LatestGradeWithEctsProjection {
    Long getSubjectId();
    String getSubjectName();
    Integer getScore();
    Integer getEcts();
}
