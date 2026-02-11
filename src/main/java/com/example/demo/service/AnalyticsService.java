package com.example.demo.service;

import com.example.demo.dto.CorrelationDTO;
import com.example.demo.dto.RiskStatsDTO;
import com.example.demo.dto.TrendAnalysisDTO;
import com.example.demo.dto.TrendPointDTO;
import com.example.demo.dto.WeightedGpaDTO;
import com.example.demo.repository.GradesRepository;
import com.example.demo.repository.projection.AttendanceGradePairProjection;
import com.example.demo.repository.projection.LatestGradeWithEctsProjection;
import com.example.demo.repository.projection.SemesterAverageProjection;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
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
