package com.example.demo.controller;

import com.example.demo.dto.GradeDTO;
import com.example.demo.model.Grades;
import com.example.demo.repository.GradesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/grades")
public class GradesController {

    private final GradesRepository gradesRepository;

    public GradesController(GradesRepository gradesRepository) {
        this.gradesRepository = gradesRepository;
    }

    @GetMapping
    public Page<GradeDTO> listLatestGrades(
            @RequestParam(required = false) Long studentId,
            @RequestParam(defaultValue = "SEM1") String semesterId,
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) Long specialtyId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Integer minScore,
            @RequestParam(required = false) Integer maxScore,
            @PageableDefault(sort = "score") Pageable pageable
    ) {
        if (minScore != null && maxScore != null && minScore > maxScore) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minScore must be <= maxScore");
        }
        Page<Grades> page = gradesRepository.findLatestWithFilters(
                semesterId,
                studentId,
                groupId,
                specialtyId,
                subjectId,
                minScore,
                maxScore,
                pageable
        );
        return page.map(g -> new GradeDTO(
                g.getId(),
                g.getStudent() != null ? g.getStudent().getId() : null,
                g.getSubject() != null ? g.getSubject().getId() : null,
                g.getSubject() != null ? g.getSubject().getName() : null,
                g.getSemester() != null ? g.getSemester().getId() : null,
                g.getScore(),
                g.getAttempt(),
                g.getDateEntered()
        ));
    }
}
