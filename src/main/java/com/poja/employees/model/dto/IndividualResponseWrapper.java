package com.poja.employees.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class IndividualResponseWrapper<T> {
    private T data;
}
