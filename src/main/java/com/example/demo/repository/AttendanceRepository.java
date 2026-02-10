package com.example.demo.repository;

import com.example.demo.model.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Page<Attendance> findByStudentIdAndSemesterId(Long studentId, String semesterId, Pageable pageable);
}
