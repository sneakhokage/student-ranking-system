package com.example.demo.service;

import com.example.demo.dto.CorrelationDTO;
import com.example.demo.dto.RiskStatsDTO;
import com.example.demo.dto.StudentClusterDTO;
import com.example.demo.dto.StudentClusterSummaryDTO;
import com.example.demo.dto.TrendAnalysisDTO;
import com.example.demo.dto.TrendPointDTO;
import com.example.demo.dto.WhatIfRequestDTO;
import com.example.demo.dto.WhatIfResultDTO;
import com.example.demo.dto.WeightedGpaDTO;
import com.example.demo.repository.GradesRepository;
import com.example.demo.repository.projection.AttendanceGradePairProjection;
import com.example.demo.repository.projection.LatestGradeWithEctsProjection;
import com.example.demo.repository.projection.SemesterAverageProjection;
import com.example.demo.repository.projection.StudentAverageProjection;
import com.example.demo.repository.projection.StudentLatestScoreProjection;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private static final int PASSING_SCORE = 50;

    private final GradesRepository gradesRepository;

    public AnalyticsService(GradesRepository gradesRepository) {
        this.gradesRepository = gradesRepository;
    }

    public WeightedGpaDTO calculateWeightedGpa(Long studentId, String semesterId) {
        List<LatestGradeWithEctsProjection> rows = gradesRepository.findLatestGradesWithEcts(studentId, semesterId);
        double weightedSum = 0.0;
        int ectsSum = 0;
        for (LatestGradeWithEctsProjection row : rows) {
            if (row.getScore() == null || row.getEcts() == null) {
                continue;
            }
            weightedSum += row.getScore() * row.getEcts();
            ectsSum += row.getEcts();
        }
        double gpa = ectsSum == 0 ? 0.0 : weightedSum / ectsSum;
        return new WeightedGpaDTO(studentId, semesterId, gpa);
    }

    public RiskStatsDTO calculateRisk(Long studentId, String semesterId) {
        List<LatestGradeWithEctsProjection> rows = gradesRepository.findLatestGradesWithEcts(studentId, semesterId);
        long debtCount = rows.stream()
                .filter(r -> r.getScore() != null && r.getScore() < PASSING_SCORE)
                .count();
        return new RiskStatsDTO(studentId, semesterId, debtCount, debtCount > 0);
    }

    public TrendAnalysisDTO calculateTrend(Long studentId) {
        List<TrendPointDTO> points = gradesRepository.findStudentSemesterAveragesLatest(studentId).stream()
                .sorted(Comparator.comparing(SemesterAverageProjection::getSemesterId))
                .map(r -> new TrendPointDTO(r.getSemesterId(), r.getAverageScore()))
                .collect(Collectors.toList());

        double trendCoefficient = calculateSlope(points);
        String direction;
        if (trendCoefficient > 0.01) {
            direction = "UP";
        } else if (trendCoefficient < -0.01) {
            direction = "DOWN";
        } else {
            direction = "STABLE";
        }
        return new TrendAnalysisDTO(studentId, points, trendCoefficient, direction);
    }

    public CorrelationDTO calculateAttendancePerformanceCorrelation(Long studentId, String semesterId) {
        List<AttendanceGradePairProjection> pairs = gradesRepository.findAttendanceGradePairsLatest(studentId, semesterId);
        double pearson = calculatePearson(pairs);
        return new CorrelationDTO(studentId, semesterId, pairs.size(), pearson);
    }

    public WhatIfResultDTO calculateWhatIf(WhatIfRequestDTO request) {
        if (request.getStudentId() == null || request.getSemesterId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "studentId and semesterId are required");
        }

        List<LatestGradeWithEctsProjection> currentRows =
                gradesRepository.findLatestGradesWithEcts(request.getStudentId(), request.getSemesterId());
        Map<Long, Integer> scoreBySubject = new HashMap<>();
        Map<Long, Integer> ectsBySubject = new HashMap<>();

        for (LatestGradeWithEctsProjection row : currentRows) {
            if (row.getSubjectId() == null || row.getScore() == null || row.getEcts() == null) {
                continue;
            }
            scoreBySubject.put(row.getSubjectId(), row.getScore());
            ectsBySubject.put(row.getSubjectId(), row.getEcts());
        }

        double currentGpa = computeWeighted(scoreBySubject, ectsBySubject);

        if (request.getExpectedScores() != null) {
            request.getExpectedScores().forEach(item -> {
                if (item == null || item.getSubjectId() == null || item.getExpectedScore() == null) {
                    return;
                }
                if (!ectsBySubject.containsKey(item.getSubjectId())) {
                    return;
                }
                int bounded = Math.max(0, Math.min(100, item.getExpectedScore()));
                scoreBySubject.put(item.getSubjectId(), bounded);
            });
        }

        double projectedGpa = computeWeighted(scoreBySubject, ectsBySubject);
        return new WhatIfResultDTO(
                request.getStudentId(),
                request.getSemesterId(),
                currentGpa,
                projectedGpa,
                projectedGpa - currentGpa
        );
    }

    public StudentClusterSummaryDTO classifyStudents(String semesterId) {
        List<StudentAverageProjection> averages = gradesRepository.findStudentAveragesLatestBySemester(semesterId);
        List<StudentLatestScoreProjection> latestScores = gradesRepository.findStudentLatestScoresBySemester(semesterId);

        Map<Long, Long> debtCountsByStudent = latestScores.stream()
                .filter(s -> s.getScore() != null && s.getScore() < PASSING_SCORE)
                .collect(Collectors.groupingBy(StudentLatestScoreProjection::getStudentId, Collectors.counting()));

        List<StudentClusterDTO> students = averages.stream()
                .map(avg -> {
                    long debtCount = debtCountsByStudent.getOrDefault(avg.getStudentId(), 0L);
                    String cluster = resolveCluster(avg.getAverageScore(), debtCount);
                    return new StudentClusterDTO(
                            avg.getStudentId(),
                            avg.getFullName(),
                            semesterId,
                            avg.getAverageScore(),
                            debtCount,
                            cluster
                    );
                })
                .collect(Collectors.toList());

        int topCount = (int) students.stream().filter(s -> "TOP".equals(s.getCluster())).count();
        int middleCount = (int) students.stream().filter(s -> "MIDDLE".equals(s.getCluster())).count();
        int riskCount = (int) students.stream().filter(s -> "RISK".equals(s.getCluster())).count();

        return new StudentClusterSummaryDTO(
                semesterId,
                students.size(),
                topCount,
                middleCount,
                riskCount,
                students
        );
    }

    private String resolveCluster(Double averageScore, long debtCount) {
        if (debtCount > 0) {
            return "RISK";
        }
        double score = averageScore == null ? 0.0 : averageScore;
        if (score >= 85.0) {
            return "TOP";
        }
        return "MIDDLE";
    }

    private double computeWeighted(Map<Long, Integer> scoreBySubject, Map<Long, Integer> ectsBySubject) {
        double weightedSum = 0.0;
        int ectsSum = 0;
        for (Map.Entry<Long, Integer> entry : scoreBySubject.entrySet()) {
            Integer score = entry.getValue();
            Integer ects = ectsBySubject.get(entry.getKey());
            if (score == null || ects == null) {
                continue;
            }
            weightedSum += score * ects;
            ectsSum += ects;
        }
        return ectsSum == 0 ? 0.0 : weightedSum / ectsSum;
    }

    private double calculateSlope(List<TrendPointDTO> points) {
        int n = points.size();
        if (n < 2) {
            return 0.0;
        }

        double sumX = 0.0;
        double sumY = 0.0;
        double sumXY = 0.0;
        double sumX2 = 0.0;

        for (int i = 0; i < n; i++) {
            double x = i + 1;
            double y = points.get(i).getAverageScore() == null ? 0.0 : points.get(i).getAverageScore();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double denominator = n * sumX2 - sumX * sumX;
        if (denominator == 0.0) {
            return 0.0;
        }
        return (n * sumXY - sumX * sumY) / denominator;
    }

    private double calculatePearson(List<AttendanceGradePairProjection> pairs) {
        int n = pairs.size();
        if (n < 2) {
            return 0.0;
        }

        double sumX = 0.0;
        double sumY = 0.0;
        double sumXY = 0.0;
        double sumX2 = 0.0;
        double sumY2 = 0.0;

        for (AttendanceGradePairProjection pair : pairs) {
            double x = pair.getAttendancePercentage() == null ? 0.0 : pair.getAttendancePercentage();
            double y = pair.getScore() == null ? 0.0 : pair.getScore();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
            sumY2 += y * y;
        }

        double numerator = n * sumXY - sumX * sumY;
        double denominator = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));
        if (denominator == 0.0) {
            return 0.0;
        }
        return numerator / denominator;
    }
}
