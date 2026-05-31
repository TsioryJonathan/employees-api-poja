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

  Page<Intern> findByManagerId(Long managerId, Pageable pageable);

  boolean existsByEmail(String email);

  Optional<Intern> findByEmail(String email);
}
