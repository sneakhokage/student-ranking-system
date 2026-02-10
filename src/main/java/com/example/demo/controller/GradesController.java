package com.example.demo.controller;

import com.example.demo.dto.GradeDTO;
import com.example.demo.model.Grades;
import com.example.demo.repository.GradesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/grades")
public class GradesController {

    private final GradesRepository gradesRepository;

    public GradesController(GradesRepository gradesRepository) {
        this.gradesRepository = gradesRepository;
    }

    @GetMapping
    public Page<GradeDTO> listLatestGrades(
            @RequestParam Long studentId,
            @RequestParam String semesterId,
            @PageableDefault(sort = "score") Pageable pageable
    ) {
        Page<Grades> page = gradesRepository.findLatestByStudentAndSemester(studentId, semesterId, pageable);
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
