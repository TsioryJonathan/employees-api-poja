package com.poja.employees.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseWrapper<T> {
  private List<T> data;
  private long total;
}
