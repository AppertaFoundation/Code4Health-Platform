package cloud.operon.platform.service.impl;

import cloud.operon.platform.domain.Operino;
import cloud.operon.platform.domain.Patient;
import cloud.operon.platform.service.MailService;
import cloud.operon.platform.service.OperinoProvisioner;
import cloud.operon.platform.service.OperinoService;
import cloud.operon.platform.service.util.ThinkEhrRestClient;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Service Implementation for provisioning Operinos.
 */
@Service
@Transactional
@RabbitListener(queues = "operinos")
@ConfigurationProperties(prefix = "provisioner", ignoreUnknownFields = false)
public class OperinoProvisionerImpl implements InitializingBean, OperinoProvisioner {

    private final Logger log = LoggerFactory.getLogger(OperinoProvisionerImpl.class);
    String domainUrl;
    String cdrUrl;
    String explorerUrl;
    String subjectNamespace;
    String username;
    String password;
    String agentName;
    List<Patient> patients = new ArrayList<>();

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    OperinoService operinoService;

    @Autowired
    ThinkEhrRestClient thinkEhrRestClient;

    @Autowired
    MailService mailService;

    @Override
    @RabbitHandler
    public void receive(@Payload Operino operino) {
        log.info("Received operino {}", operino);
        // now build variables for posting to ehrscape provisioner
        String plainCreds = username + ":" + password;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // create Map of data to be posted for domain creation
        Map<String, String> data= operinoService.getConfigForOperino(operino);
        // save token for use later on
        String token = data.get("token");
        // remove token before submitting data
        data.remove("token");

        // post data to api
        HttpEntity<Map<String, String>> getRequst = new HttpEntity<>(headers);
        log.info("getRequest = " + getRequst);
        try {
            ResponseEntity<List> getResponse = restTemplate.exchange(domainUrl, HttpMethod.GET, getRequst, List.class);
            log.debug("getResponse = " + getResponse);
            if(getResponse.getStatusCode() == HttpStatus.OK && !getResponse.getBody().contains(operino.getDomain())){

                HttpEntity<Map<String, String>> domainRequest = new HttpEntity<>(data, headers);
                log.debug("domainRequest = " + domainRequest);
                ResponseEntity<String> domainResponse = restTemplate.postForEntity(domainUrl, domainRequest, String.class);
                log.debug("domainResponse = " + domainResponse);
                if(domainResponse.getStatusCode() == HttpStatus.CREATED) {
                    // create template headers for xml data post
                    HttpHeaders templateHeaders = new HttpHeaders();
                    templateHeaders.setContentType(MediaType.APPLICATION_XML);
                    templateHeaders.add("Authorization", "Basic " + token);
                    // upload various templates - we have to upload at least on template as work around fo EhrExplorer bug
                    thinkEhrRestClient.uploadTemplate(templateHeaders, "sample_requests/vital-signs/vital-signs-template.xml");
                    // now if user has requested provisioning, we upload other templates and generated data
                    if (operino.getProvision()) {
                        thinkEhrRestClient.uploadTemplate(templateHeaders, "sample_requests/allergies/allergies-template.xml");
                        thinkEhrRestClient.uploadTemplate(templateHeaders, "sample_requests/lab-results/lab-results-template.xml");
                        thinkEhrRestClient.uploadTemplate(templateHeaders, "sample_requests/orders/orders-template.xml");
                        thinkEhrRestClient.uploadTemplate(templateHeaders, "sample_requests/problems/problems-template.xml");
                        thinkEhrRestClient.uploadTemplate(templateHeaders, "sample_requests/procedures/procedures-template.xml");

                        // now call provisioner url with parameters to populate dummy data against problem diagnosis template

                        // now call ehrscape_provisioner endpoint with map and a parameter for data file
                        templateHeaders.setContentType(MediaType.APPLICATION_JSON);
                        for(Patient p : patients) {
                            try {
                                // create patient
                                String patientId = thinkEhrRestClient.createPatient(templateHeaders, p);
                                log.debug("Created patient with Id = {}", patientId);
                                // create ehr
                                String ehrId = thinkEhrRestClient.createEhr(p, templateHeaders, subjectNamespace, p.getNhsNumber(), agentName);
                                log.debug("Created ehr with Id = {}", ehrId);
                                // now upload compositions against each template loaded above
                                // -- first process vital signs template compositions
                                // create composition file path
                                String compositionPath = "sample_requests/vital-signs/vital-signs-composition.json";
                                String compositionId = thinkEhrRestClient.createComposition(templateHeaders, ehrId, "Vital Signs Encounter (Composition)", agentName, compositionPath);
                                log.debug("Created composition with Id = {}", compositionId);
                                // -- first process allergy template compositions
                                for(int i=1; i<7; i++){
                                    // create composition file path
                                    compositionPath = "sample_requests/allergies/AllergiesList_" +i+"FLAT.json";
                                    compositionId = thinkEhrRestClient.createComposition(templateHeaders, ehrId, "IDCR Allergies List.v0", agentName, compositionPath);
                                    log.debug("Created composition with Id = {}", compositionId);
                                }
                                // -- next process lab order compositions
                                for(int i=1; i<13; i++){
                                    // create composition file path
                                    compositionPath = "sample_requests/orders/IDCR_Lab_Order_FLAT_" +i+".json";
                                    compositionId = thinkEhrRestClient.createComposition(templateHeaders, ehrId, "IDCR - Laboratory Order.v0", agentName, compositionPath);
                                    log.debug("Created composition with Id = {}", compositionId);
                                }
                                // -- next process procedure compositions
                                for(int i=1; i<7; i++){
                                    // create composition file path
                                    compositionPath = "sample_requests/procedures/IDCR_Procedures_List_FLAT_" +i+".json";
                                    compositionId = thinkEhrRestClient.createComposition(templateHeaders, ehrId, "IDCR Procedures List.v0", agentName, compositionPath);
                                    log.debug("Created composition with Id = {}", compositionId);
                                }
                                // -- next process lab result compositions
                                for(int i=1; i<13; i++){
                                    // create composition file path
                                    compositionPath = "sample_requests/lab-results/IDCR_Lab_Report_INPUT_FLAT_" +i+".json";
                                    compositionId = thinkEhrRestClient.createComposition(templateHeaders, ehrId, "IDCR - Laboratory Test Report.v0", agentName, compositionPath);
                                    log.debug("Created composition with Id = {}", compositionId);
                                }

                            } catch (IOException e) {
                                log.error("Error processing json to submit for composition. Nested exception is : ", e);
                            }
                        }

                    }

                    // if entire provisioning has been completed
                    Map<String, String> configMap = operinoService.getConfigForOperino(operino);
                    configMap.put("cdr", cdrUrl);
                    configMap.put("explorer", explorerUrl);
                    mailService.sendProvisioningCompletionEmail(operino, configMap);

                } else {
                    log.error("Unable to create domain for operino {}", operino);
                }
            } else {
                log.error("Unable to verify domain {} does not already exist. So operino will NOT be processed.", operino.getDomain());
            }
        } catch (HttpClientErrorException e) {
            log.error("Error looking up domain using domain id {}. Nested exception is : {}", operino.getDomain(), e);
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // verify we are able to connect to thinkehr instance
        String plainCreds = username + ":" + password;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // connect to api
        HttpEntity<Map<String, String>> getRequest = new HttpEntity<>(headers);
        log.info("getRequest = " + getRequest);
        ResponseEntity<List> getResponse;
        try {
            getResponse = restTemplate.exchange(domainUrl, HttpMethod.GET, getRequest, List.class);
        }
        catch (HttpClientErrorException e) {
            throw new RuntimeException("Unable to connect to ThinkEHR backend specified by: " + domainUrl);
        }

        log.info("getResponse = " + getResponse);
        if(getResponse == null || getResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Unable to connect to ThinkEHR backend specified by: " + domainUrl);
        }

        // load patients from files
        for(int i=0; i< 5; i++) {
            patients.addAll(loadPatientsList("data/patients" + (i + 1) + ".csv"));
            log.info("Loaded {} patients from file {}", patients.size(), "data/patients"+i+".csv");
        }
        log.info("Final number of patients = {}", patients.size());
    }

    public List<Patient> loadPatientsList(String fileName) {
        try {
            CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
            CsvMapper mapper = new CsvMapper();
            MappingIterator<Patient> mappingIterator = mapper.reader(Patient.class).with(bootstrapSchema).readValues(OperinoProvisionerImpl.class.getClassLoader().getResourceAsStream(fileName));
            return mappingIterator.readAll();
        } catch (Exception e) {
            log.error("Error occurred while loading object list from file " + fileName, e);
            return Collections.emptyList();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setExplorerUrl(String explorerUrl) {
        this.explorerUrl = explorerUrl;
    }

    public void setCdrUrl(String cdrUrl) {
        this.cdrUrl = cdrUrl;
    }

    public void setDomainUrl(String domainUrl) {
        this.domainUrl = domainUrl;
    }

    public void setSubjectNamespace(String subjectNamespace) {
        this.subjectNamespace = subjectNamespace;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }
}
