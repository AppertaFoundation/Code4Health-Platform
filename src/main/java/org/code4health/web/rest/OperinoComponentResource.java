package org.code4health.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.code4health.domain.OperinoComponent;
import org.code4health.service.OperinoComponentService;
import org.code4health.web.rest.util.HeaderUtil;
import org.code4health.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing OperinoComponent.
 */
@RestController
@RequestMapping("/api")
public class OperinoComponentResource {

    private final Logger log = LoggerFactory.getLogger(OperinoComponentResource.class);

    private static final String ENTITY_NAME = "operinoComponent";
        
    private final OperinoComponentService operinoComponentService;

    public OperinoComponentResource(OperinoComponentService operinoComponentService) {
        this.operinoComponentService = operinoComponentService;
    }

    /**
     * POST  /operino-components : Create a new operinoComponent.
     *
     * @param operinoComponent the operinoComponent to create
     * @return the ResponseEntity with status 201 (Created) and with body the new operinoComponent, or with status 400 (Bad Request) if the operinoComponent has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/operino-components")
    @Timed
    public ResponseEntity<OperinoComponent> createOperinoComponent(@Valid @RequestBody OperinoComponent operinoComponent) throws URISyntaxException {
        log.debug("REST request to save OperinoComponent : {}", operinoComponent);
        if (operinoComponent.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new operinoComponent cannot already have an ID")).body(null);
        }
        OperinoComponent result = operinoComponentService.save(operinoComponent);
        return ResponseEntity.created(new URI("/api/operino-components/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /operino-components : Updates an existing operinoComponent.
     *
     * @param operinoComponent the operinoComponent to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated operinoComponent,
     * or with status 400 (Bad Request) if the operinoComponent is not valid,
     * or with status 500 (Internal Server Error) if the operinoComponent couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/operino-components")
    @Timed
    public ResponseEntity<OperinoComponent> updateOperinoComponent(@Valid @RequestBody OperinoComponent operinoComponent) throws URISyntaxException {
        log.debug("REST request to update OperinoComponent : {}", operinoComponent);
        if (operinoComponent.getId() == null) {
            return createOperinoComponent(operinoComponent);
        }
        OperinoComponent result = operinoComponentService.save(operinoComponent);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, operinoComponent.getId().toString()))
            .body(result);
    }

    /**
     * GET  /operino-components : get all the operinoComponents.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of operinoComponents in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/operino-components")
    @Timed
    public ResponseEntity<List<OperinoComponent>> getAllOperinoComponents(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of OperinoComponents");
        Page<OperinoComponent> page = operinoComponentService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/operino-components");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /operino-components/:id : get the "id" operinoComponent.
     *
     * @param id the id of the operinoComponent to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the operinoComponent, or with status 404 (Not Found)
     */
    @GetMapping("/operino-components/{id}")
    @Timed
    public ResponseEntity<OperinoComponent> getOperinoComponent(@PathVariable Long id) {
        log.debug("REST request to get OperinoComponent : {}", id);
        OperinoComponent operinoComponent = operinoComponentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(operinoComponent));
    }

    /**
     * DELETE  /operino-components/:id : delete the "id" operinoComponent.
     *
     * @param id the id of the operinoComponent to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/operino-components/{id}")
    @Timed
    public ResponseEntity<Void> deleteOperinoComponent(@PathVariable Long id) {
        log.debug("REST request to delete OperinoComponent : {}", id);
        operinoComponentService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/operino-components?query=:query : search for the operinoComponent corresponding
     * to the query.
     *
     * @param query the query of the operinoComponent search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/operino-components")
    @Timed
    public ResponseEntity<List<OperinoComponent>> searchOperinoComponents(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of OperinoComponents for query {}", query);
        Page<OperinoComponent> page = operinoComponentService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/operino-components");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
