package com.example.demo.controller;

import com.example.demo.dto.StudentRankingDTO;
import com.example.demo.model.Students;
import com.example.demo.service.RankingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/top")
    public List<StudentRankingDTO> getTopStudents(
            @RequestParam(defaultValue = "SEM1") String semesterId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return rankingService.getRanking(
                semesterId,
                RankingService.RankingScope.STREAM,
                null,
                null,
                RankingService.DebtFilter.ALL,
                RankingService.RankingSortBy.PERFORMANCE,
                "DESC",
                limit
        );
    }

    @GetMapping("/list")
    public List<StudentRankingDTO> getRanking(
            @RequestParam(defaultValue = "SEM1") String semesterId,
            @RequestParam(defaultValue = "STREAM") RankingService.RankingScope scope,
            @RequestParam(required = false) Long facultyId,
            @RequestParam(required = false) Students.FormOfStudy formOfStudy,
            @RequestParam(defaultValue = "ALL") RankingService.DebtFilter debtFilter,
            @RequestParam(defaultValue = "PERFORMANCE") RankingService.RankingSortBy sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(defaultValue = "100") Integer limit
    ) {
        return rankingService.getRanking(
                semesterId,
                scope,
                facultyId,
                formOfStudy,
                debtFilter,
                sortBy,
                direction,
                limit
        );
    }
}
