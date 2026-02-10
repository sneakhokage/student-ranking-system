package com.example.demo.controller;

import com.example.demo.dto.SemesterDTO;
import com.example.demo.model.Semester;
import com.example.demo.repository.SemesterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/semesters")
public class SemestersController {

    private final SemesterRepository semesterRepository;

    public SemestersController(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    @GetMapping
    public Page<SemesterDTO> listSemesters(@PageableDefault(sort = "id") Pageable pageable) {
        Page<Semester> page = semesterRepository.findAll(pageable);
        return page.map(s -> new SemesterDTO(
                s.getId(),
                s.getName()
        ));
    }
}
