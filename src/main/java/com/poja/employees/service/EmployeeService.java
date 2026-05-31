package com.poja.employees.service;

import com.poja.employees.endpoint.rest.dto.EmployeeRequest;
import com.poja.employees.mapper.EmployeeMapper;
import com.poja.employees.model.Employee;
import com.poja.employees.model.dto.EmployeeResponse;
import com.poja.employees.model.dto.IndividualResponseWrapper;
import com.poja.employees.model.dto.ResponseWrapper;
import com.poja.employees.model.exception.NotFoundException;
import com.poja.employees.repository.DepartmentRepository;
import com.poja.employees.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    public ResponseWrapper<EmployeeResponse> getAllEmployees(Pageable pageable) {
        Page<Employee> employeePage = employeeRepository.findAll(pageable);

        Page<EmployeeResponse> responsePage = employeePage.map(employeeMapper::toDTO);

        return new ResponseWrapper<>(
                responsePage.getContent(),
                employeePage.getTotalElements()
        );
    }
    public IndividualResponseWrapper<EmployeeResponse> getEmployeeById(long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee with id: " + id + " not found"));
        EmployeeResponse dto = employeeMapper.toDTO(employee);
        return new IndividualResponseWrapper<>(dto);
    }

    public IndividualResponseWrapper<EmployeeResponse> createEmployee(EmployeeRequest request) {
        Employee employee = employeeMapper.toEntity(request);
        if (request.getDepartmentId() != null) {
            var department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new NotFoundException("Department not found with id: " + request.getDepartmentId()));
            employee.setDepartment(department);
        }

        Employee savedEmployee = employeeRepository.save(employee);
        EmployeeResponse response = employeeMapper.toDTO(savedEmployee);

        return new IndividualResponseWrapper<>(response);
    }
}