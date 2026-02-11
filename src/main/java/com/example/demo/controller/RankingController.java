package com.example.demo.controller;

import com.example.demo.dto.StudentRankingDTO;
import com.example.demo.repository.GradesRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final GradesRepository gradesRepository;

    public RankingController(GradesRepository gradesRepository) {
        this.gradesRepository = gradesRepository;
    }

    @GetMapping("/top")
    public List<StudentRankingDTO> getTopStudents(
            @RequestParam(defaultValue = "SEM1") String semesterId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return gradesRepository.findTopRankingBySemester(semesterId, PageRequest.of(0, limit)).stream()
                .map(r -> new StudentRankingDTO(
                        r.getFullName(),
                        r.getGroupName(),
                        r.getAverageScore()
                ))
                .collect(Collectors.toList());
    }
}
