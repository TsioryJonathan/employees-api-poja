# Intern CRUD API Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add full CRUD REST API for Intern entities at `/api/interns`, following the existing Employee pattern.

**Architecture:** New InternController, InternService, InternMapper, InternRepository + InternRequest/InternResponse DTOs. Follows exact same pattern as Employee CRUD. No existing files need modification.

**Tech Stack:** Spring Boot 3.2.2, Java 21, JPA/Hibernate, Lombok

---

### Task 1: DTOs — InternRequest and InternResponse

**Files:**
- Create: `src/main/java/com/poja/employees/endpoint/rest/dto/InternRequest.java`
- Create: `src/main/java/com/poja/employees/model/dto/InternResponse.java`

- [ ] **Step 1: Create InternRequest.java**

```java
package com.poja.employees.endpoint.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternRequest {
  private String name;
  private String email;
  private Boolean isRemunerated;
  private Double remuneration;
  private Long managerId;
  private Long departmentId;
}
```

- [ ] **Step 2: Create InternResponse.java**

```java
package com.poja.employees.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InternResponse {
  private Long id;
  private String name;
  private String email;
  private Boolean isRemunerated;
  private Double remuneration;
  private String department;
  private String managerName;
  private Long departmentId;
  private Long managerId;
}
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/poja/employees/endpoint/rest/dto/InternRequest.java src/main/java/com/poja/employees/model/dto/InternResponse.java
git commit -m "feat: create intern DTOs"
```

---

### Task 2: InternRepository

**Files:**
- Create: `src/main/java/com/poja/employees/repository/InternRepository.java`

- [ ] **Step 1: Create InternRepository.java**

```java
package com.poja.employees.repository;

import com.poja.employees.model.Intern;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternRepository extends JpaRepository<Intern, Long> {
  @Override
  Page<Intern> findAll(Pageable pageable);

  boolean existsByEmail(String email);

  Optional<Intern> findByEmail(String email);
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/poja/employees/repository/InternRepository.java
git commit -m "feat: create intern repository"
```

---

### Task 3: InternMapper

**Files:**
- Create: `src/main/java/com/poja/employees/mapper/InternMapper.java`

- [ ] **Step 1: Create InternMapper.java**

```java
package com.poja.employees.mapper;

import com.poja.employees.endpoint.rest.dto.InternRequest;
import com.poja.employees.model.Intern;
import com.poja.employees.model.dto.InternResponse;
import org.springframework.stereotype.Component;

@Component
public class InternMapper {

  public InternResponse toDTO(Intern intern) {
    if (intern == null) {
      return null;
    }
    InternResponse response = new InternResponse();
    response.setId(intern.getId());
    response.setName(intern.getName());
    response.setEmail(intern.getEmail());
    response.setIsRemunerated(intern.getIsRemunerated());
    response.setRemuneration(intern.getRemuneration());
    if (intern.getDepartment() != null) {
      response.setDepartment(intern.getDepartment().getName());
      response.setDepartmentId(intern.getDepartment().getId());
    }
    if (intern.getManager() != null) {
      response.setManagerName(intern.getManager().getName());
      response.setManagerId(intern.getManager().getId());
    }
    return response;
  }

  public Intern toEntity(InternRequest request) {
    if (request == null) return null;

    Intern intern = new Intern();
    intern.setName(request.getName());
    intern.setEmail(request.getEmail());
    intern.setIsRemunerated(request.getIsRemunerated());
    intern.setRemuneration(request.getRemuneration());

    return intern;
  }

  public void updateEntity(InternRequest request, Intern existingIntern) {
    if (request == null) return;

    existingIntern.setName(request.getName());
    existingIntern.setEmail(request.getEmail());
    existingIntern.setIsRemunerated(request.getIsRemunerated());
    existingIntern.setRemuneration(request.getRemuneration());
  }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/poja/employees/mapper/InternMapper.java
git commit -m "feat: create intern mapper"
```

---

### Task 4: InternService

**Files:**
- Create: `src/main/java/com/poja/employees/service/InternService.java`

- [ ] **Step 1: Create InternService.java**

```java
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

  public ResponseWrapper<InternResponse> getAllInterns(Pageable pageable) {
    Page<Intern> internPage = internRepository.findAll(pageable);
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
              .orElseThrow(
                  () -> new NotFoundException("Intern with id: " + id + " not found"));
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
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/poja/employees/service/InternService.java
git commit -m "feat: create intern service"
```

---

### Task 5: InternController

**Files:**
- Create: `src/main/java/com/poja/employees/endpoint/rest/controller/administration/InternController.java`

- [ ] **Step 1: Create InternController.java**

```java
package com.poja.employees.endpoint.rest.controller.administration;

import com.poja.employees.endpoint.rest.dto.InternRequest;
import com.poja.employees.model.dto.IndividualResponseWrapper;
import com.poja.employees.model.dto.InternResponse;
import com.poja.employees.model.dto.ResponseWrapper;
import com.poja.employees.service.InternService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interns")
@AllArgsConstructor
public class InternController {

  private final InternService internService;

  @GetMapping
  public ResponseEntity<ResponseWrapper<InternResponse>> getAllInterns(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) String order) {
    Pageable pageable = buildPageable(page, size, sort, order);
    ResponseWrapper<InternResponse> response = internService.getAllInterns(pageable);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<IndividualResponseWrapper<InternResponse>> getInternById(
      @PathVariable long id) {
    return ResponseEntity.status(HttpStatus.OK).body(internService.getInternById(id));
  }

  @PostMapping
  public ResponseEntity<IndividualResponseWrapper<InternResponse>> createIntern(
      @RequestBody InternRequest internRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(internService.createIntern(internRequest));
  }

  @PutMapping("/{id}")
  public ResponseEntity<IndividualResponseWrapper<InternResponse>> updateIntern(
      @PathVariable long id, @RequestBody InternRequest internRequest) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(internService.updateIntern(id, internRequest));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<IndividualResponseWrapper<String>> deleteIntern(@PathVariable long id) {
    return ResponseEntity.status(HttpStatus.OK).body(internService.deleteIntern(id));
  }

  private Pageable buildPageable(int page, int size, String sort, String order) {
    if (sort != null && !sort.isEmpty()) {
      Sort.Direction direction =
          order != null && order.equalsIgnoreCase("DESC")
              ? Sort.Direction.DESC
              : Sort.Direction.ASC;
      return PageRequest.of(page, size, Sort.by(direction, sort));
    }
    return PageRequest.of(page, size);
  }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/poja/employees/endpoint/rest/controller/administration/InternController.java
git commit -m "feat: add intern CRUD controller"
```

---

### Task 6: Format and final verification

- [ ] **Step 1: Format all Java files**

```bash
./format.sh
```

- [ ] **Step 2: Verify no unformatted files remain**

```bash
git diff --exit-code
```

- [ ] **Step 3: Commit formatting**

```bash
git add -A
git commit -m "chore: format code"
```
