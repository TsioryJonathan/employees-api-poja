package com.poja.employees.repository;

import com.poja.employees.model.Intern;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InternRepository extends JpaRepository<Intern, Long> {

  @Query(
      nativeQuery = true,
      value =
          "SELECT i.* FROM intern i LEFT JOIN department d ON d.id = i.id_department WHERE "
              + "(:managerId IS NULL OR i.id_manager = :managerId) AND "
              + "(:q IS NULL OR LOWER(CAST(i.name AS text)) LIKE LOWER(CONCAT('%', :q, '%'))) AND "
              + "(:department IS NULL OR d.name = :department) AND "
              + "(:isRemunerated IS NULL OR i.is_remunerated = :isRemunerated)",
      countQuery =
          "SELECT COUNT(*) FROM intern i LEFT JOIN department d ON d.id = i.id_department WHERE "
              + "(:managerId IS NULL OR i.id_manager = :managerId) AND "
              + "(:q IS NULL OR LOWER(CAST(i.name AS text)) LIKE LOWER(CONCAT('%', :q, '%'))) AND "
              + "(:department IS NULL OR d.name = :department) AND "
              + "(:isRemunerated IS NULL OR i.is_remunerated = :isRemunerated)")
  Page<Intern> searchInterns(
      @Param("managerId") Long managerId,
      @Param("q") String q,
      @Param("department") String department,
      @Param("isRemunerated") Boolean isRemunerated,
      Pageable pageable);

  boolean existsByEmail(String email);

  Optional<Intern> findByEmail(String email);
}
