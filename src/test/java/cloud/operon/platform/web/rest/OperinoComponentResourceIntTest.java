package cloud.operon.platform.web.rest;

import cloud.operon.platform.OperonCloudPlatformApp;
import cloud.operon.platform.domain.enumeration.HostingType;
import cloud.operon.platform.domain.enumeration.OperinoComponentType;
import cloud.operon.platform.repository.search.OperinoComponentSearchRepository;

import cloud.operon.platform.domain.OperinoComponent;
import cloud.operon.platform.repository.OperinoComponentRepository;
import cloud.operon.platform.service.OperinoComponentService;
import cloud.operon.platform.web.rest.errors.ExceptionTranslator;

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
 * Test class for the OperinoComponentResource REST controller.
 *
 * @see OperinoComponentResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OperonCloudPlatformApp.class)
public class OperinoComponentResourceIntTest {

    private static final HostingType DEFAULT_HOSTING = HostingType.N3;
    private static final HostingType UPDATED_HOSTING = HostingType.NON_N3;

    private static final Boolean DEFAULT_AVAILABILITY = false;
    private static final Boolean UPDATED_AVAILABILITY = true;

    private static final Boolean DEFAULT_APPLY_LIMITS = false;
    private static final Boolean UPDATED_APPLY_LIMITS = true;

    private static final Long DEFAULT_RECORDS_NUMBER = 1L;
    private static final Long UPDATED_RECORDS_NUMBER = 2L;

    private static final Long DEFAULT_TRANSACTIONS_LIMIT = 1L;
    private static final Long UPDATED_TRANSACTIONS_LIMIT = 2L;

    private static final Long DEFAULT_DISK_SPACE = 1L;
    private static final Long UPDATED_DISK_SPACE = 2L;

    private static final Long DEFAULT_COMPUTE_RESOURCE_LIMIT = 1L;
    private static final Long UPDATED_COMPUTE_RESOURCE_LIMIT = 2L;

    private static final OperinoComponentType DEFAULT_TYPE = OperinoComponentType.CDR;
    private static final OperinoComponentType UPDATED_TYPE = OperinoComponentType.DEMOGRAPHICS;

    @Autowired
    private OperinoComponentRepository operinoComponentRepository;

    @Autowired
    private OperinoComponentService operinoComponentService;

    @Autowired
    private OperinoComponentSearchRepository operinoComponentSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restOperinoComponentMockMvc;

    private OperinoComponent operinoComponent;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OperinoComponentResource operinoComponentResource = new OperinoComponentResource(operinoComponentService);
        this.restOperinoComponentMockMvc = MockMvcBuilders.standaloneSetup(operinoComponentResource)
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
    public static OperinoComponent createEntity(EntityManager em) {
        OperinoComponent operinoComponent = new OperinoComponent()
                .hosting(DEFAULT_HOSTING)
                .availability(DEFAULT_AVAILABILITY)
                .applyLimits(DEFAULT_APPLY_LIMITS)
                .recordsNumber(DEFAULT_RECORDS_NUMBER)
                .transactionsLimit(DEFAULT_TRANSACTIONS_LIMIT)
                .diskSpace(DEFAULT_DISK_SPACE)
                .computeResourceLimit(DEFAULT_COMPUTE_RESOURCE_LIMIT)
                .type(DEFAULT_TYPE);
        return operinoComponent;
    }

    @Before
    public void initTest() {
        operinoComponentSearchRepository.deleteAll();
        operinoComponent = createEntity(em);
    }

    @Test
    @Transactional
    public void createOperinoComponent() throws Exception {
        int databaseSizeBeforeCreate = operinoComponentRepository.findAll().size();

        // Create the OperinoComponent

        restOperinoComponentMockMvc.perform(post("/api/operino-components")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(operinoComponent)))
            .andExpect(status().isCreated());

        // Validate the OperinoComponent in the database
        List<OperinoComponent> operinoComponentList = operinoComponentRepository.findAll();
        assertThat(operinoComponentList).hasSize(databaseSizeBeforeCreate + 1);
        OperinoComponent testOperinoComponent = operinoComponentList.get(operinoComponentList.size() - 1);
        assertThat(testOperinoComponent.getHosting()).isEqualTo(DEFAULT_HOSTING);
        assertThat(testOperinoComponent.isAvailability()).isEqualTo(DEFAULT_AVAILABILITY);
        assertThat(testOperinoComponent.isApplyLimits()).isEqualTo(DEFAULT_APPLY_LIMITS);
        assertThat(testOperinoComponent.getRecordsNumber()).isEqualTo(DEFAULT_RECORDS_NUMBER);
        assertThat(testOperinoComponent.getTransactionsLimit()).isEqualTo(DEFAULT_TRANSACTIONS_LIMIT);
        assertThat(testOperinoComponent.getDiskSpace()).isEqualTo(DEFAULT_DISK_SPACE);
        assertThat(testOperinoComponent.getComputeResourceLimit()).isEqualTo(DEFAULT_COMPUTE_RESOURCE_LIMIT);
        assertThat(testOperinoComponent.getType()).isEqualTo(DEFAULT_TYPE);

        // Validate the OperinoComponent in Elasticsearch
        OperinoComponent operinoComponentEs = operinoComponentSearchRepository.findOne(testOperinoComponent.getId());
        assertThat(operinoComponentEs).isEqualToComparingFieldByField(testOperinoComponent);
    }

    @Test
    @Transactional
    public void createOperinoComponentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = operinoComponentRepository.findAll().size();

        // Create the OperinoComponent with an existing ID
        OperinoComponent existingOperinoComponent = new OperinoComponent();
        existingOperinoComponent.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOperinoComponentMockMvc.perform(post("/api/operino-components")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingOperinoComponent)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<OperinoComponent> operinoComponentList = operinoComponentRepository.findAll();
        assertThat(operinoComponentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkHostingIsRequired() throws Exception {
        int databaseSizeBeforeTest = operinoComponentRepository.findAll().size();
        // set the field null
        operinoComponent.setHosting(null);

        // Create the OperinoComponent, which fails.

        restOperinoComponentMockMvc.perform(post("/api/operino-components")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(operinoComponent)))
            .andExpect(status().isBadRequest());

        List<OperinoComponent> operinoComponentList = operinoComponentRepository.findAll();
        assertThat(operinoComponentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = operinoComponentRepository.findAll().size();
        // set the field null
        operinoComponent.setType(null);

        // Create the OperinoComponent, which fails.

        restOperinoComponentMockMvc.perform(post("/api/operino-components")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(operinoComponent)))
            .andExpect(status().isBadRequest());

        List<OperinoComponent> operinoComponentList = operinoComponentRepository.findAll();
        assertThat(operinoComponentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOperinoComponents() throws Exception {
        // Initialize the database
        operinoComponentRepository.saveAndFlush(operinoComponent);

        // Get all the operinoComponentList
        restOperinoComponentMockMvc.perform(get("/api/operino-components?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operinoComponent.getId().intValue())))
            .andExpect(jsonPath("$.[*].hosting").value(hasItem(DEFAULT_HOSTING.toString())))
            .andExpect(jsonPath("$.[*].availability").value(hasItem(DEFAULT_AVAILABILITY.booleanValue())))
            .andExpect(jsonPath("$.[*].applyLimits").value(hasItem(DEFAULT_APPLY_LIMITS.booleanValue())))
            .andExpect(jsonPath("$.[*].recordsNumber").value(hasItem(DEFAULT_RECORDS_NUMBER.intValue())))
            .andExpect(jsonPath("$.[*].transactionsLimit").value(hasItem(DEFAULT_TRANSACTIONS_LIMIT.intValue())))
            .andExpect(jsonPath("$.[*].diskSpace").value(hasItem(DEFAULT_DISK_SPACE.intValue())))
            .andExpect(jsonPath("$.[*].computeResourceLimit").value(hasItem(DEFAULT_COMPUTE_RESOURCE_LIMIT.intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    public void getOperinoComponent() throws Exception {
        // Initialize the database
        operinoComponentRepository.saveAndFlush(operinoComponent);

        // Get the operinoComponent
        restOperinoComponentMockMvc.perform(get("/api/operino-components/{id}", operinoComponent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(operinoComponent.getId().intValue()))
            .andExpect(jsonPath("$.hosting").value(DEFAULT_HOSTING.toString()))
            .andExpect(jsonPath("$.availability").value(DEFAULT_AVAILABILITY.booleanValue()))
            .andExpect(jsonPath("$.applyLimits").value(DEFAULT_APPLY_LIMITS.booleanValue()))
            .andExpect(jsonPath("$.recordsNumber").value(DEFAULT_RECORDS_NUMBER.intValue()))
            .andExpect(jsonPath("$.transactionsLimit").value(DEFAULT_TRANSACTIONS_LIMIT.intValue()))
            .andExpect(jsonPath("$.diskSpace").value(DEFAULT_DISK_SPACE.intValue()))
            .andExpect(jsonPath("$.computeResourceLimit").value(DEFAULT_COMPUTE_RESOURCE_LIMIT.intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOperinoComponent() throws Exception {
        // Get the operinoComponent
        restOperinoComponentMockMvc.perform(get("/api/operino-components/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOperinoComponent() throws Exception {
        // Initialize the database
        operinoComponentService.save(operinoComponent);

        int databaseSizeBeforeUpdate = operinoComponentRepository.findAll().size();

        // Update the operinoComponent
        OperinoComponent updatedOperinoComponent = operinoComponentRepository.findOne(operinoComponent.getId());
        updatedOperinoComponent
                .hosting(UPDATED_HOSTING)
                .availability(UPDATED_AVAILABILITY)
                .applyLimits(UPDATED_APPLY_LIMITS)
                .recordsNumber(UPDATED_RECORDS_NUMBER)
                .transactionsLimit(UPDATED_TRANSACTIONS_LIMIT)
                .diskSpace(UPDATED_DISK_SPACE)
                .computeResourceLimit(UPDATED_COMPUTE_RESOURCE_LIMIT)
                .type(UPDATED_TYPE);

        restOperinoComponentMockMvc.perform(put("/api/operino-components")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedOperinoComponent)))
            .andExpect(status().isOk());

        // Validate the OperinoComponent in the database
        List<OperinoComponent> operinoComponentList = operinoComponentRepository.findAll();
        assertThat(operinoComponentList).hasSize(databaseSizeBeforeUpdate);
        OperinoComponent testOperinoComponent = operinoComponentList.get(operinoComponentList.size() - 1);
        assertThat(testOperinoComponent.getHosting()).isEqualTo(UPDATED_HOSTING);
        assertThat(testOperinoComponent.isAvailability()).isEqualTo(UPDATED_AVAILABILITY);
        assertThat(testOperinoComponent.isApplyLimits()).isEqualTo(UPDATED_APPLY_LIMITS);
        assertThat(testOperinoComponent.getRecordsNumber()).isEqualTo(UPDATED_RECORDS_NUMBER);
        assertThat(testOperinoComponent.getTransactionsLimit()).isEqualTo(UPDATED_TRANSACTIONS_LIMIT);
        assertThat(testOperinoComponent.getDiskSpace()).isEqualTo(UPDATED_DISK_SPACE);
        assertThat(testOperinoComponent.getComputeResourceLimit()).isEqualTo(UPDATED_COMPUTE_RESOURCE_LIMIT);
        assertThat(testOperinoComponent.getType()).isEqualTo(UPDATED_TYPE);

        // Validate the OperinoComponent in Elasticsearch
        OperinoComponent operinoComponentEs = operinoComponentSearchRepository.findOne(testOperinoComponent.getId());
        assertThat(operinoComponentEs).isEqualToComparingFieldByField(testOperinoComponent);
    }

    @Test
    @Transactional
    public void updateNonExistingOperinoComponent() throws Exception {
        int databaseSizeBeforeUpdate = operinoComponentRepository.findAll().size();

        // Create the OperinoComponent

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOperinoComponentMockMvc.perform(put("/api/operino-components")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(operinoComponent)))
            .andExpect(status().isCreated());

        // Validate the OperinoComponent in the database
        List<OperinoComponent> operinoComponentList = operinoComponentRepository.findAll();
        assertThat(operinoComponentList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOperinoComponent() throws Exception {
        // Initialize the database
        operinoComponentService.save(operinoComponent);

        int databaseSizeBeforeDelete = operinoComponentRepository.findAll().size();

        // Get the operinoComponent
        restOperinoComponentMockMvc.perform(delete("/api/operino-components/{id}", operinoComponent.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean operinoComponentExistsInEs = operinoComponentSearchRepository.exists(operinoComponent.getId());
        assertThat(operinoComponentExistsInEs).isFalse();

        // Validate the database is empty
        List<OperinoComponent> operinoComponentList = operinoComponentRepository.findAll();
        assertThat(operinoComponentList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOperinoComponent() throws Exception {
        // Initialize the database
        operinoComponentService.save(operinoComponent);

        // Search the operinoComponent
        restOperinoComponentMockMvc.perform(get("/api/_search/operino-components?query=id:" + operinoComponent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operinoComponent.getId().intValue())))
            .andExpect(jsonPath("$.[*].hosting").value(hasItem(DEFAULT_HOSTING.toString())))
            .andExpect(jsonPath("$.[*].availability").value(hasItem(DEFAULT_AVAILABILITY.booleanValue())))
            .andExpect(jsonPath("$.[*].applyLimits").value(hasItem(DEFAULT_APPLY_LIMITS.booleanValue())))
            .andExpect(jsonPath("$.[*].recordsNumber").value(hasItem(DEFAULT_RECORDS_NUMBER.intValue())))
            .andExpect(jsonPath("$.[*].transactionsLimit").value(hasItem(DEFAULT_TRANSACTIONS_LIMIT.intValue())))
            .andExpect(jsonPath("$.[*].diskSpace").value(hasItem(DEFAULT_DISK_SPACE.intValue())))
            .andExpect(jsonPath("$.[*].computeResourceLimit").value(hasItem(DEFAULT_COMPUTE_RESOURCE_LIMIT.intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OperinoComponent.class);
    }
}
