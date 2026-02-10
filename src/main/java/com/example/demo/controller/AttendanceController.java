package com.example.demo.controller;

import com.example.demo.dto.AttendanceDTO;
import com.example.demo.model.Attendance;
import com.example.demo.repository.AttendanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceRepository attendanceRepository;

    public AttendanceController(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @GetMapping
    public Page<AttendanceDTO> listAttendance(
            @RequestParam Long studentId,
            @RequestParam String semesterId,
            @PageableDefault(sort = "percentage") Pageable pageable
    ) {
        Page<Attendance> page = attendanceRepository.findByStudentIdAndSemesterId(studentId, semesterId, pageable);
        return page.map(a -> new AttendanceDTO(
                a.getId(),
                a.getStudent() != null ? a.getStudent().getId() : null,
                a.getSubject() != null ? a.getSubject().getId() : null,
                a.getSemester() != null ? a.getSemester().getId() : null,
                a.getPercentage()
        ));
    }
}
