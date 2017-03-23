package org.code4health.repository;

import org.code4health.domain.Operino;
import org.code4health.domain.OperinoComponent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the OperinoComponent entity.
 */
@SuppressWarnings("unused")
public interface OperinoComponentRepository extends JpaRepository<OperinoComponent,Long> {

    Page<OperinoComponent> findByOperino(Operino operino, Pageable pageable);

    OperinoComponent findByIdAndOperino(Long id, Operino operino);
}
