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
