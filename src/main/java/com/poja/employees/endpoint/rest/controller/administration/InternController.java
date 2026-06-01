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
      @RequestParam(required = false) String order,
      @RequestParam(required = false) Long managerId,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String department,
      @RequestParam(required = false) Boolean isRemunerated) {
    Pageable pageable = buildPageable(page, size, sort, order);
    ResponseWrapper<InternResponse> response =
        internService.getAllInterns(pageable, managerId, q, department, isRemunerated);
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
    return ResponseEntity.status(HttpStatus.OK).body(internService.updateIntern(id, internRequest));
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
