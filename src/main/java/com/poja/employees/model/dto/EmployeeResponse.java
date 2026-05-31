package com.poja.employees.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmployeeResponse {
    private Long id;
    private String name;
    private String email;
    private String department;
    private Boolean isActive;
    private Double salary;
    private List<Long> internIds = new ArrayList<>();
}
