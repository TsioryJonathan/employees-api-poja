package com.poja.employees.handler;

import com.poja.employees.model.exception.CannotDeleteEmployeeException;
import com.poja.employees.model.exception.DuplicateEmailException;
import com.poja.employees.model.exception.NotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException e) {
    Map<String, String> map = new HashMap<>();
    map.put("error", e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
  }

  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<Map<String, String>> handleDuplicateEmailException(
      DuplicateEmailException e) {
    Map<String, String> map = new HashMap<>();
    map.put("error", e.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
  }

  @ExceptionHandler(CannotDeleteEmployeeException.class)
  public ResponseEntity<Map<String, String>> handleCannotDeleteEmployeeException(
      CannotDeleteEmployeeException e) {
    Map<String, String> map = new HashMap<>();
    map.put("error", e.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
  }
}
