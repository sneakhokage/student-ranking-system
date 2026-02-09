package com.example.demo.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity @Data
public class Semester {
    @Id
    private String id;
    private String name;
}