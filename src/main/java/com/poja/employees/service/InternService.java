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
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InternService {

  private final InternRepository internRepository;
  private final DepartmentRepository departmentRepository;
  private final EmployeeRepository employeeRepository;
  private final InternMapper internMapper;

  public ResponseWrapper<InternResponse> getAllInterns(Pageable pageable, Long managerId) {
    Page<Intern> internPage =
        (managerId != null)
            ? internRepository.findByManagerId(managerId, pageable)
            : internRepository.findAll(pageable);
    Page<InternResponse> responsePage = internPage.map(internMapper::toDTO);
    return new ResponseWrapper<>(responsePage.getContent(), internPage.getTotalElements());
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
