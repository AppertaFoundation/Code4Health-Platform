package org.code4health.service.impl;

import org.code4health.service.OperinoService;
import org.code4health.domain.Operino;
import org.code4health.repository.OperinoRepository;
import org.code4health.repository.search.OperinoSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Operino.
 */
@Service
@Transactional
public class OperinoServiceImpl implements OperinoService{

    private final Logger log = LoggerFactory.getLogger(OperinoServiceImpl.class);
    
    private final OperinoRepository operinoRepository;

    private final OperinoSearchRepository operinoSearchRepository;

    public OperinoServiceImpl(OperinoRepository operinoRepository, OperinoSearchRepository operinoSearchRepository) {
        this.operinoRepository = operinoRepository;
        this.operinoSearchRepository = operinoSearchRepository;
    }

    /**
     * Save a operino.
     *
     * @param operino the entity to save
     * @return the persisted entity
     */
    @Override
    public Operino save(Operino operino) {
        log.debug("Request to save Operino : {}", operino);
        Operino result = operinoRepository.save(operino);
        operinoSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the operinos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Operino> findAll(Pageable pageable) {
        log.debug("Request to get all Operinos");
        Page<Operino> result = operinoRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one operino by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Operino findOne(Long id) {
        log.debug("Request to get Operino : {}", id);
        Operino operino = operinoRepository.findOne(id);
        return operino;
    }

    /**
     *  Delete the  operino by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Operino : {}", id);
        operinoRepository.delete(id);
        operinoSearchRepository.delete(id);
    }

    /**
     * Search for the operino corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Operino> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Operinos for query {}", query);
        Page<Operino> result = operinoSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
