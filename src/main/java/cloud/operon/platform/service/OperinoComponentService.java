package cloud.operon.platform.service;

import cloud.operon.platform.domain.Operino;
import cloud.operon.platform.domain.OperinoComponent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing OperinoComponent.
 */
public interface OperinoComponentService {

    /**
     * Save a operinoComponent.
     *
     * @param operinoComponent the entity to save
     * @return the persisted entity
     */
    OperinoComponent save(OperinoComponent operinoComponent);

    /**
     *  Get all the operinoComponents.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<OperinoComponent> findAll(Pageable pageable);

    /**
     *  Get all the components for Operino.
     *
     *  @param operino the operino to retrieve components for
     *  @return the list of components
     */
    Page<OperinoComponent> findAllByOperino(Operino operino, Pageable pageable);

    /**
     *  Get the "id" operinoComponent.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    OperinoComponent findOne(Long id);

    OperinoComponent findOneByOperino(Long id, Operino operino);

    /**
     *  Delete the "id" operinoComponent.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the operinoComponent corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<OperinoComponent> search(String query, Pageable pageable);
}
