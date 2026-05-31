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
