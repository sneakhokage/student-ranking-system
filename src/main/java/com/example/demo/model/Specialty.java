package com.example.demo.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity @Data
public class Specialty {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code;
    @ManyToOne @JoinColumn(name = "department_id")
    private Department department;
}