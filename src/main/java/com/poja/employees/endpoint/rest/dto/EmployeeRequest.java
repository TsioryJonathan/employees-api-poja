package com.poja.employees.endpoint.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequest {
    private String name;
    private String email;
    private Double salary;
    private Boolean isActive;
    private Long departmentId;
}