package com.poja.employees.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ResponseWrapper<T> {
    private List<T> data;
    private long total;
}
