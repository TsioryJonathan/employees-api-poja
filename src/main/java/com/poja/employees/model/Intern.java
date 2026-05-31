package com.poja.employees.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "intern")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Intern extends User{

    @Column(nullable = false)
    private Boolean isRemunerated;

    private Double remuneration;

    @ManyToOne
    @JoinColumn(name = "id_manager")
    private Employee manager;
}
