package com.example.demo.controller;

import com.example.demo.dto.StudentListDTO;
import com.example.demo.model.Students;
import com.example.demo.repository.StudentsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
public class StudentsController {

    private final StudentsRepository studentsRepository;

    public StudentsController(StudentsRepository studentsRepository) {
        this.studentsRepository = studentsRepository;
    }

    @GetMapping
    public Page<StudentListDTO> listStudents(
            @RequestParam(required = false) Long groupId,
            @PageableDefault(sort = "fullName") Pageable pageable
    ) {
        Page<Students> page = (groupId == null)
                ? studentsRepository.findAll(pageable)
                : studentsRepository.findByGroupId(groupId, pageable);
        return page.map(s -> new StudentListDTO(
                s.getId(),
                s.getFullName(),
                s.getGroup() != null ? s.getGroup().getName() : null,
                s.getFormOfStudy(),
                s.getStatus()
        ));
    }
}
