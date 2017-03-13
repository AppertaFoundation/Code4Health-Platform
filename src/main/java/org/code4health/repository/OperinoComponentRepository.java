package org.code4health.repository;

import org.code4health.domain.Operino;
import org.code4health.domain.OperinoComponent;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the OperinoComponent entity.
 */
@SuppressWarnings("unused")
public interface OperinoComponentRepository extends JpaRepository<OperinoComponent,Long> {

    List<OperinoComponent> findByOperino(Operino operino);
}
