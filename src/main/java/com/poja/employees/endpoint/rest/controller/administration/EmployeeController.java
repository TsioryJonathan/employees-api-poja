package com.poja.employees.endpoint.rest.controller.administration;

import com.poja.employees.model.dto.EmployeeResponse;
import com.poja.employees.model.dto.ResponseWrapper;
import com.poja.employees.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@AllArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<EmployeeResponse>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ResponseWrapper<EmployeeResponse> response = employeeService.getAllEmployees(page, size);
        return ResponseEntity.ok(response);
    }
}