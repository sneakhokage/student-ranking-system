package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "students")
@Data
public class Students {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    public enum FormOfStudy { BUDGET, CONTRACT }
    public enum StudentStatus { ACTIVE, DROPPED }

    @Enumerated(EnumType.STRING)
    private FormOfStudy formOfStudy;

    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}