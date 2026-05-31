package com.poja.employees.repository;

import com.poja.employees.repository.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository implements JpaRepository<Employee, Long> {

}