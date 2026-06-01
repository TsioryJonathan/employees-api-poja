package com.poja.employees.repository;

import com.poja.employees.model.Employee;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

  @Query(
      nativeQuery = true,
      value =
          "SELECT e.* FROM employee e LEFT JOIN department d ON d.id = e.id_department WHERE "
              + "(:q IS NULL OR LOWER(CAST(e.name AS text)) LIKE LOWER(CONCAT('%', :q, '%'))) AND "
              + "(:department IS NULL OR d.name = :department) AND "
              + "(:isActive IS NULL OR e.is_active = :isActive)",
      countQuery =
          "SELECT COUNT(*) FROM employee e LEFT JOIN department d ON d.id = e.id_department WHERE "
              + "(:q IS NULL OR LOWER(CAST(e.name AS text)) LIKE LOWER(CONCAT('%', :q, '%'))) AND "
              + "(:department IS NULL OR d.name = :department) AND "
              + "(:isActive IS NULL OR e.is_active = :isActive)")
  Page<Employee> searchEmployees(
      @Param("q") String q,
      @Param("department") String department,
      @Param("isActive") Boolean isActive,
      Pageable pageable);

  boolean existsByEmail(String email);

  Optional<Employee> findByEmail(String email);
}
