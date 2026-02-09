package com.example.demo.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity @Data
public class Attendance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne private Students student;
    @ManyToOne private Subjects subject;
    @ManyToOne private Semester semester;
    private Integer percentage;
}