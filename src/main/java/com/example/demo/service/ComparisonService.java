package com.example.demo.service;

import com.example.demo.dto.ComparisonStatsDTO;
import com.example.demo.model.Students;
import com.example.demo.repository.GradesRepository;
import com.example.demo.repository.StudentsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ComparisonService {

    private final StudentsRepository studentsRepository;
    private final GradesRepository gradesRepository;

    public ComparisonService(StudentsRepository studentsRepository, GradesRepository gradesRepository) {
        this.studentsRepository = studentsRepository;
        this.gradesRepository = gradesRepository;
    }

    public ComparisonStatsDTO getStudentComparison(Long studentId, String semesterId) {
        Students student = studentsRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        Long groupId = student.getGroup() != null ? student.getGroup().getId() : null;
        Long specialtyId = (student.getGroup() != null && student.getGroup().getSpecialty() != null)
                ? student.getGroup().getSpecialty().getId()
                : null;

        Double studentAverage = gradesRepository.findStudentAvgLatest(studentId, semesterId);
        Double groupAverage = groupId != null ? gradesRepository.findGroupAvgLatest(groupId, semesterId) : null;
        Double specialtyAverage = specialtyId != null
                ? gradesRepository.findSpecialtyAvgLatest(specialtyId, semesterId)
                : null;
        Double streamAverage = gradesRepository.findStreamAvgLatest(semesterId);

        return new ComparisonStatsDTO(
                studentId,
                semesterId,
                studentAverage,
                groupAverage,
                specialtyAverage,
                streamAverage
        );
    }
}
