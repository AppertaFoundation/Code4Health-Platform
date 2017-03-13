package org.code4health.web.rest;

import org.code4health.Code4HealthplatformApp;

import org.code4health.domain.Operino;
import org.code4health.repository.OperinoRepository;
import org.code4health.service.OperinoComponentService;
import org.code4health.service.OperinoService;
import org.code4health.repository.search.OperinoSearchRepository;
import org.code4health.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the OperinoResource REST controller.
 *
 * @see OperinoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Code4HealthplatformApp.class)
public class OperinoResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    @Autowired
    private OperinoRepository operinoRepository;

    @Autowired
    private OperinoService operinoService;
    @Autowired
    private OperinoComponentService operinoComponentService;

    @Autowired
    private OperinoSearchRepository operinoSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restOperinoMockMvc;

    private Operino operino;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OperinoResource operinoResource = new OperinoResource(operinoService, operinoComponentService);
        this.restOperinoMockMvc = MockMvcBuilders.standaloneSetup(operinoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Operino createEntity(EntityManager em) {
        Operino operino = new Operino()
                .name(DEFAULT_NAME)
                .active(DEFAULT_ACTIVE);
        return operino;
    }

    @Before
    public void initTest() {
        operinoSearchRepository.deleteAll();
        operino = createEntity(em);
    }

    @Test
    @Transactional
    public void createOperino() throws Exception {
        int databaseSizeBeforeCreate = operinoRepository.findAll().size();

        // Create the Operino

        restOperinoMockMvc.perform(post("/api/operinos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(operino)))
            .andExpect(status().isCreated());

        // Validate the Operino in the database
        List<Operino> operinoList = operinoRepository.findAll();
        assertThat(operinoList).hasSize(databaseSizeBeforeCreate + 1);
        Operino testOperino = operinoList.get(operinoList.size() - 1);
        assertThat(testOperino.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOperino.isActive()).isEqualTo(DEFAULT_ACTIVE);

        // Validate the Operino in Elasticsearch
        Operino operinoEs = operinoSearchRepository.findOne(testOperino.getId());
        assertThat(operinoEs).isEqualToComparingFieldByField(testOperino);
    }

    @Test
    @Transactional
    public void createOperinoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = operinoRepository.findAll().size();

        // Create the Operino with an existing ID
        Operino existingOperino = new Operino();
        existingOperino.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOperinoMockMvc.perform(post("/api/operinos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingOperino)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Operino> operinoList = operinoRepository.findAll();
        assertThat(operinoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = operinoRepository.findAll().size();
        // set the field null
        operino.setName(null);

        // Create the Operino, which fails.

        restOperinoMockMvc.perform(post("/api/operinos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(operino)))
            .andExpect(status().isBadRequest());

        List<Operino> operinoList = operinoRepository.findAll();
        assertThat(operinoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOperinos() throws Exception {
        // Initialize the database
        operinoRepository.saveAndFlush(operino);

        // Get all the operinoList
        restOperinoMockMvc.perform(get("/api/operinos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operino.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    public void getOperino() throws Exception {
        // Initialize the database
        operinoRepository.saveAndFlush(operino);

        // Get the operino
        restOperinoMockMvc.perform(get("/api/operinos/{id}", operino.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(operino.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingOperino() throws Exception {
        // Get the operino
        restOperinoMockMvc.perform(get("/api/operinos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOperino() throws Exception {
        // Initialize the database
        operinoService.save(operino);

        int databaseSizeBeforeUpdate = operinoRepository.findAll().size();

        // Update the operino
        Operino updatedOperino = operinoRepository.findOne(operino.getId());
        updatedOperino
                .name(UPDATED_NAME)
                .active(UPDATED_ACTIVE);

        restOperinoMockMvc.perform(put("/api/operinos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedOperino)))
            .andExpect(status().isOk());

        // Validate the Operino in the database
        List<Operino> operinoList = operinoRepository.findAll();
        assertThat(operinoList).hasSize(databaseSizeBeforeUpdate);
        Operino testOperino = operinoList.get(operinoList.size() - 1);
        assertThat(testOperino.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOperino.isActive()).isEqualTo(UPDATED_ACTIVE);

        // Validate the Operino in Elasticsearch
        Operino operinoEs = operinoSearchRepository.findOne(testOperino.getId());
        assertThat(operinoEs).isEqualToComparingFieldByField(testOperino);
    }

    @Test
    @Transactional
    public void updateNonExistingOperino() throws Exception {
        int databaseSizeBeforeUpdate = operinoRepository.findAll().size();

        // Create the Operino

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOperinoMockMvc.perform(put("/api/operinos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(operino)))
            .andExpect(status().isCreated());

        // Validate the Operino in the database
        List<Operino> operinoList = operinoRepository.findAll();
        assertThat(operinoList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOperino() throws Exception {
        // Initialize the database
        operinoService.save(operino);

        int databaseSizeBeforeDelete = operinoRepository.findAll().size();

        // Get the operino
        restOperinoMockMvc.perform(delete("/api/operinos/{id}", operino.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean operinoExistsInEs = operinoSearchRepository.exists(operino.getId());
        assertThat(operinoExistsInEs).isFalse();

        // Validate the database is empty
        List<Operino> operinoList = operinoRepository.findAll();
        assertThat(operinoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOperino() throws Exception {
        // Initialize the database
        operinoService.save(operino);

        // Search the operino
        restOperinoMockMvc.perform(get("/api/_search/operinos?query=id:" + operino.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operino.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Operino.class);
    }
}
