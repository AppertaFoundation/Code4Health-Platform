package org.code4health.repository;

import org.code4health.domain.Operino;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Operino entity.
 */
@SuppressWarnings("unused")
public interface OperinoRepository extends JpaRepository<Operino,Long> {

    @Query("select operino from Operino operino where operino.user.login = ?#{principal.username}")
    List<Operino> findByUserIsCurrentUser();

    @Query("select operino from Operino operino where operino.user.login = :username")
    Page<Operino> findByUserIsCurrentUser(@Param("username") String username, Pageable pageable);

    @Query("select operino from Operino operino where operino.user.login = :username and operino.id = :id")
    Operino findOneByUserAndId(@Param("username") String username, @Param("id") Long operinoId);
}
