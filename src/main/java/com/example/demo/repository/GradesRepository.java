package com.example.demo.repository;

import com.example.demo.model.Grades;
import com.example.demo.repository.projection.AttendanceGradePairProjection;
import com.example.demo.repository.projection.LatestGradeWithEctsProjection;
import com.example.demo.repository.projection.RankingRowProjection;
import com.example.demo.repository.projection.RankingDetailsProjection;
import com.example.demo.repository.projection.SemesterAverageProjection;
import com.example.demo.repository.projection.StudentAverageProjection;
import com.example.demo.repository.projection.StudentLatestScoreProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import com.example.demo.model.Students;

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

    @Query("""
            SELECT g.student.fullName AS fullName,
                   COALESCE(g.student.group.name, 'No Group') AS groupName,
                   AVG(g.score) AS averageScore
            FROM Grades g
            WHERE g.semester.id = :semesterId
              AND g.attempt = (
                SELECT MAX(g2.attempt) FROM Grades g2
                WHERE g2.student.id = g.student.id
                  AND g2.subject.id = g.subject.id
                  AND g2.semester.id = :semesterId
              )
            GROUP BY g.student.id, g.student.fullName, g.student.group.name
            ORDER BY AVG(g.score) DESC
            """)
    Page<RankingRowProjection> findTopRankingBySemester(
            @Param("semesterId") String semesterId,
            Pageable pageable
    );

    @Query("""
            SELECT s.id AS studentId,
                   s.fullName AS fullName,
                   COALESCE(grp.name, 'No Group') AS groupName,
                   AVG(g.score) AS averageScore,
                   SUM(CASE WHEN g.score < 50 THEN 1 ELSE 0 END) AS debtCount,
                   s.formOfStudy AS formOfStudy,
                   fac.id AS facultyId,
                   fac.name AS facultyName
            FROM Grades g
            JOIN g.student s
            LEFT JOIN s.group grp
            LEFT JOIN grp.specialty sp
            LEFT JOIN sp.department dep
            LEFT JOIN dep.faculty fac
            WHERE g.semester.id = :semesterId
              AND (:facultyId IS NULL OR fac.id = :facultyId)
              AND (:formOfStudy IS NULL OR s.formOfStudy = :formOfStudy)
              AND (:groupPrefix IS NULL OR grp.name LIKE CONCAT(:groupPrefix, '%'))
              AND g.attempt = (
                SELECT MAX(g2.attempt) FROM Grades g2
                WHERE g2.student.id = s.id
                  AND g2.subject.id = g.subject.id
                  AND g2.semester.id = :semesterId
              )
            GROUP BY s.id, s.fullName, grp.name, s.formOfStudy, fac.id, fac.name
            """)
    java.util.List<RankingDetailsProjection> findRankingRows(
            @Param("semesterId") String semesterId,
            @Param("facultyId") Long facultyId,
            @Param("formOfStudy") Students.FormOfStudy formOfStudy,
            @Param("groupPrefix") String groupPrefix
    );

    @Query(value = """
            SELECT g.subject_id AS subjectId,
                   s.name AS subjectName,
                   g.score AS score,
                   s.ects AS ects
            FROM grades g
            JOIN subjects s ON s.id = g.subject_id
            JOIN (
                SELECT subject_id, MAX(attempt) AS max_attempt
                FROM grades
                WHERE student_id = :studentId
                  AND semester_id = :semesterId
                GROUP BY subject_id
            ) latest
              ON latest.subject_id = g.subject_id
             AND latest.max_attempt = g.attempt
            WHERE g.student_id = :studentId
              AND g.semester_id = :semesterId
            """, nativeQuery = true)
    java.util.List<LatestGradeWithEctsProjection> findLatestGradesWithEcts(
            @Param("studentId") Long studentId,
            @Param("semesterId") String semesterId
    );

    @Query("""
            SELECT g.semester.id AS semesterId, AVG(g.score) AS averageScore
            FROM Grades g
            WHERE g.student.id = :studentId
              AND g.attempt = (
                SELECT MAX(g2.attempt) FROM Grades g2
                WHERE g2.student.id = g.student.id
                  AND g2.subject.id = g.subject.id
                  AND g2.semester.id = g.semester.id
              )
            GROUP BY g.semester.id
            ORDER BY g.semester.id
            """)
    java.util.List<SemesterAverageProjection> findStudentSemesterAveragesLatest(@Param("studentId") Long studentId);

    @Query(value = """
            SELECT a.percentage AS attendancePercentage, g.score AS score
            FROM attendance a
            JOIN grades g
              ON g.student_id = a.student_id
             AND g.subject_id = a.subject_id
             AND g.semester_id = a.semester_id
            JOIN (
                SELECT subject_id, MAX(attempt) AS max_attempt
                FROM grades
                WHERE student_id = :studentId
                  AND semester_id = :semesterId
                GROUP BY subject_id
            ) latest
              ON latest.subject_id = g.subject_id
             AND latest.max_attempt = g.attempt
            WHERE a.student_id = :studentId
              AND a.semester_id = :semesterId
            """, nativeQuery = true)
    java.util.List<AttendanceGradePairProjection> findAttendanceGradePairsLatest(
            @Param("studentId") Long studentId,
            @Param("semesterId") String semesterId
    );

    @Query("""
            SELECT g.student.id AS studentId,
                   g.student.fullName AS fullName,
                   AVG(g.score) AS averageScore
            FROM Grades g
            WHERE g.semester.id = :semesterId
              AND g.attempt = (
                SELECT MAX(g2.attempt) FROM Grades g2
                WHERE g2.student.id = g.student.id
                  AND g2.subject.id = g.subject.id
                  AND g2.semester.id = :semesterId
              )
            GROUP BY g.student.id, g.student.fullName
            ORDER BY AVG(g.score) DESC
            """)
    java.util.List<StudentAverageProjection> findStudentAveragesLatestBySemester(@Param("semesterId") String semesterId);

    @Query("""
            SELECT g.student.id AS studentId, g.score AS score
            FROM Grades g
            WHERE g.semester.id = :semesterId
              AND g.attempt = (
                SELECT MAX(g2.attempt) FROM Grades g2
                WHERE g2.student.id = g.student.id
                  AND g2.subject.id = g.subject.id
                  AND g2.semester.id = :semesterId
              )
            """)
    java.util.List<StudentLatestScoreProjection> findStudentLatestScoresBySemester(@Param("semesterId") String semesterId);
}
