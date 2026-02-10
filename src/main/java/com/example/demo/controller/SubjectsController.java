package com.example.demo.controller;

import com.example.demo.dto.SubjectDTO;
import com.example.demo.model.Subjects;
import com.example.demo.repository.SubjectsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subjects")
public class SubjectsController {

    private final SubjectsRepository subjectsRepository;

    public SubjectsController(SubjectsRepository subjectsRepository) {
        this.subjectsRepository = subjectsRepository;
    }

    @GetMapping
    public Page<SubjectDTO> listSubjects(@PageableDefault(sort = "name") Pageable pageable) {
        Page<Subjects> page = subjectsRepository.findAll(pageable);
        return page.map(s -> new SubjectDTO(
                s.getId(),
                s.getName(),
                s.getCategory(),
                s.getEcts()
        ));
    }
}
