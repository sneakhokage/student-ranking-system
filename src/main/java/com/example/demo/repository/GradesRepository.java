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

    @Query("""
            SELECT g FROM Grades g
            WHERE g.semester.id = :semesterId
              AND (:studentId IS NULL OR g.student.id = :studentId)
              AND (:groupId IS NULL OR g.student.group.id = :groupId)
              AND (:specialtyId IS NULL OR g.student.group.specialty.id = :specialtyId)
              AND (:subjectId IS NULL OR g.subject.id = :subjectId)
              AND (:minScore IS NULL OR g.score >= :minScore)
              AND (:maxScore IS NULL OR g.score <= :maxScore)
              AND g.attempt = (
                SELECT MAX(g2.attempt) FROM Grades g2
                WHERE g2.student.id = g.student.id
                  AND g2.subject.id = g.subject.id
                  AND g2.semester.id = :semesterId
              )
            """)
    Page<Grades> findLatestWithFilters(
            @Param("semesterId") String semesterId,
            @Param("studentId") Long studentId,
            @Param("groupId") Long groupId,
            @Param("specialtyId") Long specialtyId,
            @Param("subjectId") Long subjectId,
            @Param("minScore") Integer minScore,
            @Param("maxScore") Integer maxScore,
            Pageable pageable
    );

    @Query("""
            SELECT AVG(g.score) FROM Grades g
            WHERE g.semester.id = :semesterId
              AND g.student.id = :studentId
              AND g.attempt = (
                SELECT MAX(g2.attempt) FROM Grades g2
                WHERE g2.student.id = g.student.id
                  AND g2.subject.id = g.subject.id
                  AND g2.semester.id = :semesterId
              )
            """)
    Double findStudentAvgLatest(@Param("studentId") Long studentId, @Param("semesterId") String semesterId);

    @Query("""
            SELECT AVG(g.score) FROM Grades g
            WHERE g.semester.id = :semesterId
              AND g.student.group.id = :groupId
              AND g.attempt = (
                SELECT MAX(g2.attempt) FROM Grades g2
                WHERE g2.student.id = g.student.id
                  AND g2.subject.id = g.subject.id
                  AND g2.semester.id = :semesterId
              )
            """)
    Double findGroupAvgLatest(@Param("groupId") Long groupId, @Param("semesterId") String semesterId);

    @Query("""
            SELECT AVG(g.score) FROM Grades g
            WHERE g.semester.id = :semesterId
              AND g.student.group.specialty.id = :specialtyId
              AND g.attempt = (
                SELECT MAX(g2.attempt) FROM Grades g2
                WHERE g2.student.id = g.student.id
                  AND g2.subject.id = g.subject.id
                  AND g2.semester.id = :semesterId
              )
            """)
    Double findSpecialtyAvgLatest(@Param("specialtyId") Long specialtyId, @Param("semesterId") String semesterId);

    @Query("""
            SELECT AVG(g.score) FROM Grades g
            WHERE g.semester.id = :semesterId
              AND g.attempt = (
                SELECT MAX(g2.attempt) FROM Grades g2
                WHERE g2.student.id = g.student.id
                  AND g2.subject.id = g.subject.id
                  AND g2.semester.id = :semesterId
              )
            """)
    Double findStreamAvgLatest(@Param("semesterId") String semesterId);
}
