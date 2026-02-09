package com.example.demo.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity @Data
public class Department {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne @JoinColumn(name = "faculty_id")
    private Faculty faculty;
}