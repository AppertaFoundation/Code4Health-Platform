package cloud.operon.platform.service.impl;

import cloud.operon.platform.domain.Operino;
import cloud.operon.platform.domain.Patient;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    String templateUrl;
    String provisionerUrl;
    String subjectNamespace;
    String username;
    String password;
    String templateToSubmit;
    String agentName;
    List<Patient> patients = new ArrayList<>();

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    OperinoService operinoService;

    @Autowired
    ThinkEhrRestClient thinkEhrRestClient;

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
                    // upload various templates
                    thinkEhrRestClient.uploadTemplate(templateHeaders, "sample_requests/allergies/allergies-template.xml");
                    thinkEhrRestClient.uploadTemplate(templateHeaders, "sample_requests/lab-results/lab-results-template.xml");
                    thinkEhrRestClient.uploadTemplate(templateHeaders, "sample_requests/orders/orders-template.xml");
                    thinkEhrRestClient.uploadTemplate(templateHeaders, "sample_requests/problems/problems-template.xml");
                    thinkEhrRestClient.uploadTemplate(templateHeaders, "sample_requests/procedures/procedures-template.xml");
                    thinkEhrRestClient.uploadTemplate(templateHeaders, "sample_requests/vital-signs/vital-signs-template.xml");

                    // now call provisioner url with parameters to populate dummy data against problem diagnosis template
                    // create data map for submission
                    Map<String, String> map = new HashMap<>();
                    map.put("username", data.get("username"));
                    map.put("password", data.get("password"));
                    map.put("baseUrl", data.get(templateUrl));
                    map.put("subjectNamespace", subjectNamespace);

                    // now call ehrscape_provisioner endpoint with map and a parameter for data file
                    templateHeaders.setContentType(MediaType.APPLICATION_JSON);
                    for(Patient p : patients) {
                        try {
                            // create patient
                            String patientId = thinkEhrRestClient.createPatient(templateHeaders, p);
                            log.info("Created patient with Id = {}", patientId);
                            // create ehr
                            String ehrId = thinkEhrRestClient.createEhr(templateHeaders, subjectNamespace, patientId, agentName);
                            log.info("Created ehr with Id = {}", ehrId);
                            // now upload compositions against each template loaded above
                            // -- first process allergy template compositions
                            for(int i=1; i<7; i++){
                                // create composition file path
                                String compositionPath = "sample_requests/allergies/AllergiesList_" +i+"FLAT.json";
                                String compositionId = thinkEhrRestClient.createComposition(templateHeaders, ehrId, "IDCR Allergies List.v0", agentName, compositionPath);
                                log.info("Created composition with Id = {}", compositionId);
                            }

                        } catch (IOException e) {
                            log.error("Error processing json to submit for composition. Nested exception is : ", e);
                        }
                    }

                } else {
                    log.error("Unable to create domain for operino {}", operino);
                }
            } else {
                log.info("Unable to verify domain {} does not already exist. So operino will NOT be processed.", operino.getDomain());
            }
        } catch (HttpClientErrorException e) {
            log.error("Error looking up domain using domain id {}. Nested exception is : {}", operino.getDomain(), e);
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try (InputStream inputStream = OperinoProvisionerImpl.class.getClassLoader().getResourceAsStream("init_template.xml")){
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line = bufReader.readLine();
            while(line != null){
                sb.append(line).append("\n");
                line = bufReader.readLine();
            }
            templateToSubmit = sb.toString();
        } catch (NullPointerException e){
            log.error("Unable to read init template from class path. Nested exception is : ", e);
        } finally {
            log.info("Init template : {}", templateToSubmit);
        }

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

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    public void setDomainUrl(String domainUrl) {
        this.domainUrl = domainUrl;
    }

    public void setProvisionerUrl(String provisionerUrl) {
        this.provisionerUrl = provisionerUrl;
    }

    public void setSubjectNamespace(String subjectNamespace) {
        this.subjectNamespace = subjectNamespace;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }
}
