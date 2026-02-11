package com.example.demo.controller;

import com.example.demo.dto.AttendanceDTO;
import com.example.demo.model.Attendance;
import com.example.demo.repository.AttendanceRepository;
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
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceRepository attendanceRepository;

    public AttendanceController(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @GetMapping
    public Page<AttendanceDTO> listAttendance(
            @RequestParam(required = false) Long studentId,
            @RequestParam(defaultValue = "SEM1") String semesterId,
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) Long specialtyId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Integer minPercentage,
            @RequestParam(required = false) Integer maxPercentage,
            @PageableDefault(sort = "percentage") Pageable pageable
    ) {
        if (minPercentage != null && maxPercentage != null && minPercentage > maxPercentage) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minPercentage must be <= maxPercentage");
        }
        Page<Attendance> page = attendanceRepository.findWithFilters(
                semesterId,
                studentId,
                groupId,
                specialtyId,
                subjectId,
                minPercentage,
                maxPercentage,
                pageable
        );
        return page.map(a -> new AttendanceDTO(
                a.getId(),
                a.getStudent() != null ? a.getStudent().getId() : null,
                a.getSubject() != null ? a.getSubject().getId() : null,
                a.getSemester() != null ? a.getSemester().getId() : null,
                a.getPercentage()
        ));
    }
}
