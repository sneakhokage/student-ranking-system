package com.example.demo.repository;

import com.example.demo.model.Grades;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

@Repository
public interface GradesRepository extends JpaRepository<Grades, Long> {
    @Query("""
            SELECT g FROM Grades g
            WHERE g.student.id = :studentId
              AND g.semester.id = :semesterId
              AND g.attempt = (
                SELECT MAX(g2.attempt) FROM Grades g2
                WHERE g2.student.id = :studentId
                  AND g2.subject.id = g.subject.id
                  AND g2.semester.id = :semesterId
              )
            """)
    Page<Grades> findLatestByStudentAndSemester(
            @Param("studentId") Long studentId,
            @Param("semesterId") String semesterId,
            Pageable pageable
    );
}
