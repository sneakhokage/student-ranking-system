package com.example.demo.repository;

import com.example.demo.model.Grades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradesRepository extends JpaRepository<Grades, Long> {
}