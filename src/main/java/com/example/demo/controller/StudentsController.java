package com.example.demo.controller;

import com.example.demo.dto.StudentListDTO;
import com.example.demo.model.Students;
import com.example.demo.repository.StudentsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.jpa.domain.Specification;
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
            @RequestParam(required = false) Long specialtyId,
            @RequestParam(required = false) Integer yearOfStudy,
            @RequestParam(required = false) Students.FormOfStudy formOfStudy,
            @RequestParam(required = false) Students.StudentStatus status,
            @PageableDefault(sort = "fullName") Pageable pageable
    ) {
        Specification<Students> spec = (root, query, cb) -> cb.conjunction();

        if (groupId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("group").get("id"), groupId));
        }
        if (specialtyId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("group").get("specialty").get("id"), specialtyId));
        }
        if (yearOfStudy != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("group").get("year"), yearOfStudy));
        }
        if (formOfStudy != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("formOfStudy"), formOfStudy));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        Page<Students> page = studentsRepository.findAll(spec, pageable);
        return page.map(s -> new StudentListDTO(
                s.getId(),
                s.getFullName(),
                s.getGroup() != null ? s.getGroup().getName() : null,
                s.getFormOfStudy(),
                s.getStatus()
        ));
    }
}
