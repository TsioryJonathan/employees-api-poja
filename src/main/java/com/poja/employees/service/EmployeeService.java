package com.poja.employees.service;

import com.poja.employees.endpoint.rest.dto.EmployeeRequest;
import com.poja.employees.mapper.EmployeeMapper;
import com.poja.employees.model.Employee;
import com.poja.employees.model.dto.EmployeeResponse;
import com.poja.employees.model.dto.IndividualResponseWrapper;
import com.poja.employees.model.dto.ResponseWrapper;
import com.poja.employees.model.exception.CannotDeleteEmployeeException;
import com.poja.employees.model.exception.DuplicateEmailException;
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
        /* check if email already exists */
        if (employeeRepository.existsByEmail(request.getEmail())){
            throw new DuplicateEmailException("Email already exists " + request.getEmail());
        }
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
    public IndividualResponseWrapper<EmployeeResponse> updateEmployee(long id, EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            Employee existing = employeeRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException("Employee with id: " + id + " not found"));
            if (existing.getId() != id) {
                throw new DuplicateEmailException("Email already exists: " + request.getEmail());
            }
        }
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee with id: " + id + " not found"));
        employeeMapper.updateEntity(request, existingEmployee);
        if (request.getDepartmentId() != null) {
            var department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new NotFoundException("Department not found with id: " + request.getDepartmentId()));
            existingEmployee.setDepartment(department);
        } else {
            existingEmployee.setDepartment(null);
        }

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        EmployeeResponse response = employeeMapper.toDTO(updatedEmployee);

        return new IndividualResponseWrapper<>(response);
    }
    public IndividualResponseWrapper<String> deleteEmployee(long id) {
        /* check if exists and check if has interns */
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee with id: " + id + " not found"));

        if (employee.getInterns() != null && !employee.getInterns().isEmpty()) {
            throw new CannotDeleteEmployeeException("Cannot delete employee with id " + id +
                    " because they manage " + employee.getInterns().size() + " intern(s)");
        }

        employeeRepository.deleteById(id);
        return new IndividualResponseWrapper<>("Employee with id " + id + " deleted successfully");
    }
}