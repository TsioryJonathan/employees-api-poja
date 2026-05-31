package com.poja.employees.model;

import jakarta.persistence.*;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "id_department")
    private Department department;
}
