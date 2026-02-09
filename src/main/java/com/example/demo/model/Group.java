package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "groups")
@Data
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "year_of_study")
    private int year;

    @ManyToOne
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;
}