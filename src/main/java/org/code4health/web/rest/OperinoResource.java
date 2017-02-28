package org.code4health.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.code4health.domain.Operino;
import org.code4health.service.OperinoService;
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
 * REST controller for managing Operino.
 */
@RestController
@RequestMapping("/api")
public class OperinoResource {

    private final Logger log = LoggerFactory.getLogger(OperinoResource.class);

    private static final String ENTITY_NAME = "operino";
        
    private final OperinoService operinoService;

    public OperinoResource(OperinoService operinoService) {
        this.operinoService = operinoService;
    }

    /**
     * POST  /operinos : Create a new operino.
     *
     * @param operino the operino to create
     * @return the ResponseEntity with status 201 (Created) and with body the new operino, or with status 400 (Bad Request) if the operino has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/operinos")
    @Timed
    public ResponseEntity<Operino> createOperino(@Valid @RequestBody Operino operino) throws URISyntaxException {
        log.debug("REST request to save Operino : {}", operino);
        if (operino.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new operino cannot already have an ID")).body(null);
        }
        Operino result = operinoService.save(operino);
        return ResponseEntity.created(new URI("/api/operinos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /operinos : Updates an existing operino.
     *
     * @param operino the operino to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated operino,
     * or with status 400 (Bad Request) if the operino is not valid,
     * or with status 500 (Internal Server Error) if the operino couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/operinos")
    @Timed
    public ResponseEntity<Operino> updateOperino(@Valid @RequestBody Operino operino) throws URISyntaxException {
        log.debug("REST request to update Operino : {}", operino);
        if (operino.getId() == null) {
            return createOperino(operino);
        }
        Operino result = operinoService.save(operino);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, operino.getId().toString()))
            .body(result);
    }

    /**
     * GET  /operinos : get all the operinos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of operinos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/operinos")
    @Timed
    public ResponseEntity<List<Operino>> getAllOperinos(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Operinos");
        Page<Operino> page = operinoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/operinos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /operinos/:id : get the "id" operino.
     *
     * @param id the id of the operino to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the operino, or with status 404 (Not Found)
     */
    @GetMapping("/operinos/{id}")
    @Timed
    public ResponseEntity<Operino> getOperino(@PathVariable Long id) {
        log.debug("REST request to get Operino : {}", id);
        Operino operino = operinoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(operino));
    }

    /**
     * DELETE  /operinos/:id : delete the "id" operino.
     *
     * @param id the id of the operino to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/operinos/{id}")
    @Timed
    public ResponseEntity<Void> deleteOperino(@PathVariable Long id) {
        log.debug("REST request to delete Operino : {}", id);
        operinoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/operinos?query=:query : search for the operino corresponding
     * to the query.
     *
     * @param query the query of the operino search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/operinos")
    @Timed
    public ResponseEntity<List<Operino>> searchOperinos(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Operinos for query {}", query);
        Page<Operino> page = operinoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/operinos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
