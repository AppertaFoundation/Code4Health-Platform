package org.code4health.service.impl;

import org.code4health.service.OperinoComponentService;
import org.code4health.domain.OperinoComponent;
import org.code4health.repository.OperinoComponentRepository;
import org.code4health.repository.search.OperinoComponentSearchRepository;
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
 * Service Implementation for managing OperinoComponent.
 */
@Service
@Transactional
public class OperinoComponentServiceImpl implements OperinoComponentService{

    private final Logger log = LoggerFactory.getLogger(OperinoComponentServiceImpl.class);
    
    private final OperinoComponentRepository operinoComponentRepository;

    private final OperinoComponentSearchRepository operinoComponentSearchRepository;

    public OperinoComponentServiceImpl(OperinoComponentRepository operinoComponentRepository, OperinoComponentSearchRepository operinoComponentSearchRepository) {
        this.operinoComponentRepository = operinoComponentRepository;
        this.operinoComponentSearchRepository = operinoComponentSearchRepository;
    }

    /**
     * Save a operinoComponent.
     *
     * @param operinoComponent the entity to save
     * @return the persisted entity
     */
    @Override
    public OperinoComponent save(OperinoComponent operinoComponent) {
        log.debug("Request to save OperinoComponent : {}", operinoComponent);
        OperinoComponent result = operinoComponentRepository.save(operinoComponent);
        operinoComponentSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the operinoComponents.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OperinoComponent> findAll(Pageable pageable) {
        log.debug("Request to get all OperinoComponents");
        Page<OperinoComponent> result = operinoComponentRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one operinoComponent by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public OperinoComponent findOne(Long id) {
        log.debug("Request to get OperinoComponent : {}", id);
        OperinoComponent operinoComponent = operinoComponentRepository.findOne(id);
        return operinoComponent;
    }

    /**
     *  Delete the  operinoComponent by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete OperinoComponent : {}", id);
        operinoComponentRepository.delete(id);
        operinoComponentSearchRepository.delete(id);
    }

    /**
     * Search for the operinoComponent corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OperinoComponent> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of OperinoComponents for query {}", query);
        Page<OperinoComponent> result = operinoComponentSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
