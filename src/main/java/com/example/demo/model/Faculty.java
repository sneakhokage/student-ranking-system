package com.example.demo.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity @Data
public class Faculty {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}