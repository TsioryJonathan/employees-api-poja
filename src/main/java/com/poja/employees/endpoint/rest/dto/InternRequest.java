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
