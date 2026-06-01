package com.poja.employees.service;

import com.poja.employees.endpoint.rest.dto.InternRequest;
import com.poja.employees.mapper.InternMapper;
import com.poja.employees.model.Employee;
import com.poja.employees.model.Intern;
import com.poja.employees.model.dto.IndividualResponseWrapper;
import com.poja.employees.model.dto.InternResponse;
import com.poja.employees.model.dto.ResponseWrapper;
import com.poja.employees.model.exception.DuplicateEmailException;
import com.poja.employees.model.exception.NotFoundException;
import com.poja.employees.repository.DepartmentRepository;
import com.poja.employees.repository.EmployeeRepository;
import com.poja.employees.repository.InternRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InternService {

  private final InternRepository internRepository;
  private final DepartmentRepository departmentRepository;
  private final EmployeeRepository employeeRepository;
  private final InternMapper internMapper;
  private final EntityManager entityManager;

  public ResponseWrapper<InternResponse> getAllInterns(
      Pageable pageable, Long managerId, String q, String department, Boolean isRemunerated) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Intern> countRoot = countQuery.from(Intern.class);
    List<Predicate> countPredicates = new ArrayList<>();
    addPredicates(cb, countRoot, countPredicates, managerId, q, department, isRemunerated);
    countQuery.select(cb.count(countRoot));
    if (!countPredicates.isEmpty()) {
      countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
    }
    long total = entityManager.createQuery(countQuery).getSingleResult();

    CriteriaQuery<Intern> dataQuery = cb.createQuery(Intern.class);
    Root<Intern> dataRoot = dataQuery.from(Intern.class);
    List<Predicate> dataPredicates = new ArrayList<>();
    addPredicates(cb, dataRoot, dataPredicates, managerId, q, department, isRemunerated);
    if (!dataPredicates.isEmpty()) {
      dataQuery.where(cb.and(dataPredicates.toArray(new Predicate[0])));
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

    TypedQuery<Intern> query = entityManager.createQuery(dataQuery);
    query.setFirstResult((int) pageable.getOffset());
    query.setMaxResults(pageable.getPageSize());
    List<Intern> interns = query.getResultList();

    Page<Intern> internPage = new PageImpl<>(interns, pageable, total);
    Page<InternResponse> responsePage = internPage.map(internMapper::toDTO);
    return new ResponseWrapper<>(responsePage.getContent(), internPage.getTotalElements());
  }

  private void addPredicates(
      CriteriaBuilder cb,
      Root<Intern> root,
      List<Predicate> predicates,
      Long managerId,
      String q,
      String department,
      Boolean isRemunerated) {
    if (managerId != null) {
      predicates.add(cb.equal(root.get("manager").get("id"), managerId));
    }
    if (q != null && !q.isEmpty()) {
      predicates.add(cb.like(cb.lower(root.get("name")), "%" + q.toLowerCase() + "%"));
    }
    if (department != null && !department.isEmpty()) {
      predicates.add(cb.equal(root.get("department").get("name"), department));
    }
    if (isRemunerated != null) {
      predicates.add(cb.equal(root.get("isRemunerated"), isRemunerated));
    }
  }

  public IndividualResponseWrapper<InternResponse> getInternById(long id) {
    Intern intern =
        internRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Intern with id: " + id + " not found"));
    return new IndividualResponseWrapper<>(internMapper.toDTO(intern));
  }

  public IndividualResponseWrapper<InternResponse> createIntern(InternRequest request) {
    if (internRepository.existsByEmail(request.getEmail())) {
      throw new DuplicateEmailException("Email already exists " + request.getEmail());
    }

    Intern intern = internMapper.toEntity(request);

    if (request.getDepartmentId() != null) {
      var department =
          departmentRepository
              .findById(request.getDepartmentId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "Department not found with id: " + request.getDepartmentId()));
      intern.setDepartment(department);
    }

    if (request.getManagerId() != null) {
      Employee manager =
          employeeRepository
              .findById(request.getManagerId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "Manager not found with id: " + request.getManagerId()));
      intern.setManager(manager);
    }

    Intern savedIntern = internRepository.save(intern);
    return new IndividualResponseWrapper<>(internMapper.toDTO(savedIntern));
  }

  public IndividualResponseWrapper<InternResponse> updateIntern(long id, InternRequest request) {
    if (internRepository.existsByEmail(request.getEmail())) {
      Intern existing =
          internRepository
              .findByEmail(request.getEmail())
              .orElseThrow(() -> new NotFoundException("Intern with id: " + id + " not found"));
      if (existing.getId() != id) {
        throw new DuplicateEmailException("Email already exists: " + request.getEmail());
      }
    }

    Intern existingIntern =
        internRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Intern with id: " + id + " not found"));

    internMapper.updateEntity(request, existingIntern);

    if (request.getDepartmentId() != null) {
      var department =
          departmentRepository
              .findById(request.getDepartmentId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "Department not found with id: " + request.getDepartmentId()));
      existingIntern.setDepartment(department);
    } else {
      existingIntern.setDepartment(null);
    }

    if (request.getManagerId() != null) {
      Employee manager =
          employeeRepository
              .findById(request.getManagerId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "Manager not found with id: " + request.getManagerId()));
      existingIntern.setManager(manager);
    } else {
      existingIntern.setManager(null);
    }

    Intern updatedIntern = internRepository.save(existingIntern);
    return new IndividualResponseWrapper<>(internMapper.toDTO(updatedIntern));
  }

  public IndividualResponseWrapper<String> deleteIntern(long id) {
    Intern intern =
        internRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Intern with id: " + id + " not found"));

    internRepository.deleteById(id);
    return new IndividualResponseWrapper<>("Intern with id " + id + " deleted successfully");
  }
}
