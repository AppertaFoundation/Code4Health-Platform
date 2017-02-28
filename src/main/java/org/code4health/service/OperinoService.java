package org.code4health.service;

import org.code4health.domain.Operino;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service Interface for managing Operino.
 */
public interface OperinoService {

    /**
     * Save a operino.
     *
     * @param operino the entity to save
     * @return the persisted entity
     */
    Operino save(Operino operino);

    /**
     *  Get all the operinos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Operino> findAll(Pageable pageable);

    /**
     *  Get the "id" operino.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Operino findOne(Long id);

    /**
     *  Delete the "id" operino.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the operino corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Operino> search(String query, Pageable pageable);
}
