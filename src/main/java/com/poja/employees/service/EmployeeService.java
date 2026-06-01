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
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmployeeService {
  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;
  private final EmployeeMapper employeeMapper;
  private final EntityManager entityManager;

  public ResponseWrapper<EmployeeResponse> getAllEmployees(
      Pageable pageable, String q, String department, Boolean isActive) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Employee> countRoot = countQuery.from(Employee.class);
    List<Predicate> predicates = new ArrayList<>();
    addPredicates(cb, countRoot, predicates, q, department, isActive);
    countQuery.select(cb.count(countRoot));
    if (!predicates.isEmpty()) {
      countQuery.where(cb.and(predicates.toArray(new Predicate[0])));
    }
    long total = entityManager.createQuery(countQuery).getSingleResult();

    CriteriaQuery<Employee> dataQuery = cb.createQuery(Employee.class);
    Root<Employee> dataRoot = dataQuery.from(Employee.class);
    predicates = new ArrayList<>();
    addPredicates(cb, dataRoot, predicates, q, department, isActive);
    if (!predicates.isEmpty()) {
      dataQuery.where(cb.and(predicates.toArray(new Predicate[0])));
    }
    if (pageable.getSort().isSorted()) {
      pageable
          .getSort()
          .forEach(
              order -> {
                var path = dataRoot.get(order.getProperty());
                dataQuery.orderBy(order.isAscending() ? cb.asc(path) : cb.desc(path));
              });
    }

    TypedQuery<Employee> query = entityManager.createQuery(dataQuery);
    query.setFirstResult((int) pageable.getOffset());
    query.setMaxResults(pageable.getPageSize());
    List<Employee> employees = query.getResultList();

    Page<Employee> employeePage = new PageImpl<>(employees, pageable, total);
    Page<EmployeeResponse> responsePage = employeePage.map(employeeMapper::toDTO);
    return new ResponseWrapper<>(responsePage.getContent(), employeePage.getTotalElements());
  }

  private void addPredicates(
      CriteriaBuilder cb,
      Root<Employee> root,
      List<Predicate> predicates,
      String q,
      String department,
      Boolean isActive) {
    if (q != null && !q.isEmpty()) {
      predicates.add(cb.like(cb.lower(root.get("name")), "%" + q.toLowerCase() + "%"));
    }
    if (department != null && !department.isEmpty()) {
      predicates.add(cb.equal(root.get("department").get("name"), department));
    }
    if (isActive != null) {
      predicates.add(cb.equal(root.get("isActive"), isActive));
    }
  }

  public IndividualResponseWrapper<EmployeeResponse> getEmployeeById(long id) {
    Employee employee =
        employeeRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Employee with id: " + id + " not found"));
    EmployeeResponse dto = employeeMapper.toDTO(employee);
    return new IndividualResponseWrapper<>(dto);
  }

  public IndividualResponseWrapper<EmployeeResponse> createEmployee(EmployeeRequest request) {
    /* check if email already exists */
    if (employeeRepository.existsByEmail(request.getEmail())) {
      throw new DuplicateEmailException("Email already exists " + request.getEmail());
    }
    Employee employee = employeeMapper.toEntity(request);
    if (request.getDepartmentId() != null) {
      var department =
          departmentRepository
              .findById(request.getDepartmentId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "Department not found with id: " + request.getDepartmentId()));
      employee.setDepartment(department);
    }

    Employee savedEmployee = employeeRepository.save(employee);
    EmployeeResponse response = employeeMapper.toDTO(savedEmployee);

    return new IndividualResponseWrapper<>(response);
  }

  public IndividualResponseWrapper<EmployeeResponse> updateEmployee(
      long id, EmployeeRequest request) {
    if (employeeRepository.existsByEmail(request.getEmail())) {
      Employee existing =
          employeeRepository
              .findByEmail(request.getEmail())
              .orElseThrow(() -> new NotFoundException("Employee with id: " + id + " not found"));
      if (existing.getId() != id) {
        throw new DuplicateEmailException("Email already exists: " + request.getEmail());
      }
    }
    Employee existingEmployee =
        employeeRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Employee with id: " + id + " not found"));
    employeeMapper.updateEntity(request, existingEmployee);
    if (request.getDepartmentId() != null) {
      var department =
          departmentRepository
              .findById(request.getDepartmentId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "Department not found with id: " + request.getDepartmentId()));
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
    Employee employee =
        employeeRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Employee with id: " + id + " not found"));

    if (employee.getInterns() != null && !employee.getInterns().isEmpty()) {
      throw new CannotDeleteEmployeeException(
          "Cannot delete employee with id "
              + id
              + " because they manage "
              + employee.getInterns().size()
              + " intern(s)");
    }

    employeeRepository.deleteById(id);
    return new IndividualResponseWrapper<>("Employee with id " + id + " deleted successfully");
  }
}
