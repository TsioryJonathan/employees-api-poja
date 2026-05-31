package com.poja.employees.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employee")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Employee extends User {
    @Column(nullable = false)
    private Double salary;

    @Column(nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "manager")
    private List<Intern> interns = new ArrayList<>();
}
