package com.poja.employees.repository;

import com.poja.employees.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Override
    Page<Employee> findAll(Pageable pageable);

    boolean existsByEmail(String email);
}