package com.poja.employees.mapper;

import com.poja.employees.endpoint.rest.dto.EmployeeRequest;
import com.poja.employees.model.Employee;
import com.poja.employees.model.Intern;
import com.poja.employees.model.dto.EmployeeResponse;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
  public EmployeeResponse toDTO(Employee employee) {
    if (employee == null) {
      return null;
    }
    EmployeeResponse employeeResponse = new EmployeeResponse();
    employeeResponse.setId(employee.getId());
    employeeResponse.setName(employee.getName());
    employeeResponse.setEmail(employee.getEmail());
    employeeResponse.setSalary(employee.getSalary());
    employeeResponse.setDepartment(employee.getDepartment().getName());
    employeeResponse.setIsActive(employee.getIsActive());
    employeeResponse.setInternIds(employee.getInterns().stream().map(Intern::getId).toList());
    return employeeResponse;
  }

  public Employee toEntity(EmployeeRequest request) {
    if (request == null) return null;

    Employee employee = new Employee();
    employee.setName(request.getName());
    employee.setEmail(request.getEmail());
    employee.setSalary(request.getSalary());
    employee.setIsActive(request.getIsActive());

    return employee;
  }

  public void updateEntity(EmployeeRequest request, Employee existingEmployee) {
    if (request == null) return;

    existingEmployee.setName(request.getName());
    existingEmployee.setEmail(request.getEmail());
    existingEmployee.setSalary(request.getSalary());
    existingEmployee.setIsActive(request.getIsActive());
  }
}
