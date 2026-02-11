package com.example.demo.service;

import com.example.demo.dto.StudentRankingDTO;
import com.example.demo.model.Students;
import com.example.demo.repository.GradesRepository;
import com.example.demo.repository.projection.RankingDetailsProjection;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class RankingService {

    public enum RankingScope { STREAM, FACULTY, UNIVERSITY }
    public enum RankingSortBy { PERFORMANCE, ALPHABET, GROUP }
    public enum DebtFilter { ALL, WITH_DEBTS, WITHOUT_DEBTS }

    private final GradesRepository gradesRepository;

    public RankingService(GradesRepository gradesRepository) {
        this.gradesRepository = gradesRepository;
    }

    public List<StudentRankingDTO> getRanking(
            String semesterId,
            RankingScope scope,
            Long facultyId,
            Students.FormOfStudy formOfStudy,
            DebtFilter debtFilter,
            RankingSortBy sortBy,
            String direction,
            Integer limit
    ) {
        RankingScope safeScope = scope == null ? RankingScope.STREAM : scope;
        RankingSortBy safeSort = sortBy == null ? RankingSortBy.PERFORMANCE : sortBy;
        DebtFilter safeDebtFilter = debtFilter == null ? DebtFilter.ALL : debtFilter;
        String safeDirection = direction == null ? "DESC" : direction.trim().toUpperCase(Locale.ROOT);

        if (!safeDirection.equals("ASC") && !safeDirection.equals("DESC")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "direction must be ASC or DESC");
        }
        if (safeScope == RankingScope.FACULTY && facultyId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "facultyId is required for FACULTY scope");
        }
        if (limit != null && limit <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit must be > 0");
        }

        Long resolvedFacultyId = safeScope == RankingScope.FACULTY ? facultyId : null;
        String resolvedGroupPrefix = safeScope == RankingScope.STREAM ? "ІПЗ-" : null;
        List<StudentRankingDTO> rows = gradesRepository.findRankingRows(
                        semesterId,
                        resolvedFacultyId,
                        formOfStudy,
                        resolvedGroupPrefix
                ).stream()
                .map(this::toDto)
                .filter(r -> matchesDebtFilter(r, safeDebtFilter))
                .collect(Collectors.toList());

        Comparator<StudentRankingDTO> comparator = comparatorBySort(safeSort, safeDirection);
        rows.sort(comparator);

        int max = limit == null ? rows.size() : Math.min(limit, rows.size());
        return rows.subList(0, max);
    }

    private Comparator<StudentRankingDTO> comparatorBySort(RankingSortBy sortBy, String direction) {
        Comparator<StudentRankingDTO> comparator;
        if (sortBy == RankingSortBy.ALPHABET) {
            comparator = Comparator.comparing(
                    r -> r.getFullName() == null ? "" : r.getFullName().toLowerCase(Locale.ROOT)
            );
        } else if (sortBy == RankingSortBy.GROUP) {
            comparator = Comparator.comparing(
                    r -> r.getGroupName() == null ? "" : r.getGroupName().toLowerCase(Locale.ROOT)
            );
        } else {
            comparator = Comparator.comparing(
                    r -> r.getAverageScore() == null ? 0.0 : r.getAverageScore()
            );
        }
        if ("DESC".equals(direction)) {
            comparator = comparator.reversed();
        }
        return Comparator
                .comparingInt((StudentRankingDTO r) -> hasDebts(r) ? 1 : 0)
                .thenComparing(comparator)
                .thenComparing(
                r -> r.getFullName() == null ? "" : r.getFullName().toLowerCase(Locale.ROOT)
                );
    }

    private boolean matchesDebtFilter(StudentRankingDTO row, DebtFilter filter) {
        if (filter == DebtFilter.WITH_DEBTS) {
            return hasDebts(row);
        }
        if (filter == DebtFilter.WITHOUT_DEBTS) {
            return !hasDebts(row);
        }
        return true;
    }

    private boolean hasDebts(StudentRankingDTO row) {
        return row.getDebtCount() != null && row.getDebtCount() > 0;
    }

    private StudentRankingDTO toDto(RankingDetailsProjection row) {
        return new StudentRankingDTO(
                row.getStudentId(),
                row.getFullName(),
                row.getGroupName(),
                row.getAverageScore(),
                row.getDebtCount(),
                row.getFormOfStudy(),
                row.getFacultyId(),
                row.getFacultyName()
        );
    }
}
