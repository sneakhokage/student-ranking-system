package com.example.demo.controller;

import com.example.demo.dto.FacultyDTO;
import com.example.demo.repository.FacultyRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/faculties")
public class FacultyController {

    private final FacultyRepository facultyRepository;

    public FacultyController(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @GetMapping
    public List<FacultyDTO> listFaculties() {
        return facultyRepository.findAll(Sort.by("name")).stream()
                .map(f -> new FacultyDTO(f.getId(), f.getName()))
                .collect(Collectors.toList());
    }
}
