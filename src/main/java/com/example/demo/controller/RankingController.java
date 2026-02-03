package com.example.demo.controller;

import com.example.demo.dto.StudentRankingDTO;
import com.example.demo.repository.StudentsRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final StudentsRepository studentsRepository;

    public RankingController(StudentsRepository studentsRepository) {
        this.studentsRepository = studentsRepository;
    }

    @GetMapping("/top")
    public List<StudentRankingDTO> getTopStudents() {
        return studentsRepository.findAll().stream()
                .limit(10)
                .map(s -> new StudentRankingDTO(
                        s.getFullName(),
                        s.getGroup() != null ? s.getGroup().getName() : "Без групи",
                        0.0 //потрібна реальна формула від anal dev xd
                ))
                .collect(Collectors.toList());
    }
}