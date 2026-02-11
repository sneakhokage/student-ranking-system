package com.example.demo.repository;

import com.example.demo.model.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Page<Attendance> findByStudentIdAndSemesterId(Long studentId, String semesterId, Pageable pageable);

    @Query("""
            SELECT a FROM Attendance a
            WHERE a.semester.id = :semesterId
              AND (:studentId IS NULL OR a.student.id = :studentId)
              AND (:groupId IS NULL OR a.student.group.id = :groupId)
              AND (:specialtyId IS NULL OR a.student.group.specialty.id = :specialtyId)
              AND (:subjectId IS NULL OR a.subject.id = :subjectId)
              AND (:minPercentage IS NULL OR a.percentage >= :minPercentage)
              AND (:maxPercentage IS NULL OR a.percentage <= :maxPercentage)
            """)
    Page<Attendance> findWithFilters(
            @Param("semesterId") String semesterId,
            @Param("studentId") Long studentId,
            @Param("groupId") Long groupId,
            @Param("specialtyId") Long specialtyId,
            @Param("subjectId") Long subjectId,
            @Param("minPercentage") Integer minPercentage,
            @Param("maxPercentage") Integer maxPercentage,
            Pageable pageable
    );
}
