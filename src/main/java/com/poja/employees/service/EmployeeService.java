package com.poja.employees.service;

import com.poja.employees.mapper.EmployeeMapper;
import com.poja.employees.model.Employee;
import com.poja.employees.model.dto.EmployeeResponse;
import com.poja.employees.model.dto.ResponseWrapper;
import com.poja.employees.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public ResponseWrapper<EmployeeResponse> getAllEmployees(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeRepository.findAll(pageable);

        Page<EmployeeResponse> responsePage = employeePage.map(employeeMapper::toDTO);

        return new ResponseWrapper<>(
                responsePage.getContent(),
                employeePage.getTotalElements()
        );
    }
}