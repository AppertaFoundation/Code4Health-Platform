package cloud.operon.platform.service.util;

import cloud.operon.platform.domain.Patient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A naive rest client for ThinkEhr
 */
@Service
@Transactional
@ConfigurationProperties(prefix = "thinkehr", ignoreUnknownFields = false)
public class ThinkEhrRestClient {

    String baseUrl;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ObjectMapper objectMapper;
    private final Logger log = LoggerFactory.getLogger(ThinkEhrRestClient.class);

    ResponseEntity<Map> doPost(String url, HttpHeaders httpHeaders, Object body) throws JsonProcessingException {

        HttpEntity<Object> request = new HttpEntity<>(objectMapper.writeValueAsString(body), httpHeaders);
        log.debug("request = " + request);

        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, request, Map.class);
        log.debug("responseEntity = {}", responseEntity);

        return responseEntity;
    }

    public String createPatient(HttpHeaders httpHeaders, Patient patient) throws JsonProcessingException {

        ResponseEntity<Map> responseEntity = doPost(baseUrl + "demographics/party", httpHeaders, transformPatient(patient));
        log.debug("responseEntity = {}", responseEntity);
        log.debug("responseEntity.getBody() = {}", responseEntity.getBody());
        Map response = responseEntity.getBody();
        if(responseEntity.getStatusCode() == HttpStatus.CREATED) {
            Map<String, String> meta = (Map<String, String>) response.get("meta");
            String href = meta.get("href");
            return href.substring(href.lastIndexOf('/') + 1);
        } else {
            throw new RuntimeException("Unable to create patient");
        }
    }

    public String createEhr(Patient patient, HttpHeaders httpHeaders, String subjectNamespace, String subjectId, String commiterName) throws IOException {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "ehr")
                .queryParam("subjectNamespace", subjectNamespace)
                .queryParam("subjectId", subjectId)
                .queryParam("commiterName", commiterName);

        // read body from existing json template and fill in values
        InputStream inputStream = ThinkEhrRestClient.class.getClassLoader().getResourceAsStream("sample_requests/ehrStatusBody.json");
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line = bufReader.readLine();
        while(line != null){
            sb.append(line).append("\n");
            line = bufReader.readLine();
        }

        // now build string
        String bodyString = sb.toString();
        // now update data with values from patient
        bodyString = bodyString.replaceAll("<subjectId>", subjectId);
        bodyString = bodyString.replaceAll("<subjectNamespace>", subjectNamespace);
        bodyString = bodyString.replaceAll("<gender>", patient.getGender());
        bodyString = bodyString.replaceAll("<birth_year>", String.valueOf(patient.getDateOfBirth().getYear()));
        HttpEntity<Object> request = new HttpEntity<>(bodyString, httpHeaders);
        log.debug("request = " + request);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.POST,
                request,
                Map.class);
        log.debug("responseEntity = {}", responseEntity);
        log.debug("responseEntity.getBody() = {}", responseEntity.getBody());
        Map response = responseEntity.getBody();
        if(responseEntity.getStatusCode() == HttpStatus.CREATED) {
            return response.get("ehrId").toString();
        } else {
            throw new RuntimeException("Unable to create ehr");
        }
    }

    public String createComposition(HttpHeaders httpHeaders, String ehrId, String templateId, String commiterName, String compositionPath) throws IOException {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "composition")
                .queryParam("ehrId", ehrId)
                .queryParam("templateId", templateId)
                .queryParam("commiterName", commiterName)
                .queryParam("format", "FLAT");

        Map data = objectMapper.readValue(ThinkEhrRestClient.class.getClassLoader().getResourceAsStream(compositionPath), Map.class);
        HttpEntity<Object> request = new HttpEntity<>(objectMapper.writeValueAsString(data), httpHeaders);
        log.debug("request = " + request);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.POST,
                request,
                Map.class);
        log.debug("responseEntity = {}", responseEntity);
        log.debug("responseEntity.getBody() = {}", responseEntity.getBody());
        Map response = responseEntity.getBody();
        if(responseEntity.getStatusCode() == HttpStatus.CREATED) {
            return response.get("compositionUid").toString();
        } else {
            throw new RuntimeException("Unable to create composition");
        }
    }

    public void uploadTemplate(HttpHeaders httpHeaders, String templatePath) {

        String templateToSubmit = null;
        try (InputStream inputStream = ThinkEhrRestClient.class.getClassLoader().getResourceAsStream(templatePath)){
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line = bufReader.readLine();
            while(line != null){
                sb.append(line).append("\n");
                line = bufReader.readLine();
            }
            // now set final value of templateToSubmit
            templateToSubmit = sb.toString();

            HttpEntity<String> templateRequest = new HttpEntity<>(templateToSubmit, httpHeaders);
            log.debug("templateRequest = " + templateRequest);
            ResponseEntity templateResponse = restTemplate.postForEntity(baseUrl+"template", templateRequest, String.class);
            log.debug("templateResponse = " + templateResponse);

        } catch (NullPointerException | IOException e){
            log.error("Unable to read init template from class path. Nested exception is : ", e);
        } finally {
            log.info("Init template : {}", templateToSubmit);
        }
    }

    /**
     * Utility method for wrapping {@link Patient} object in the format that ThinkEhr expects
     * @param patient
     * @return
     */
    private Map<String, Object> transformPatient(Patient patient) {
        Map<String, Object> map = new HashMap<>();
        map.put("dateOfBirth", patient.getDateOfBirth());
        map.put("firstNames", patient.getForename());
        map.put("gender", patient.getGender().toUpperCase());
        map.put("lastNames", patient.getSurname());
        map.put("version", 1);
        // create address key
        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("address", patient.getAddress1().concat(" ").concat(patient.getAddress2()).concat(" ").concat(patient.getAddress3()));
        addressMap.put("version", 1);
        map.put("address", addressMap);

        // create partyAdditionalInfo
        Map<String, Object> titleMap = new HashMap<>();
        titleMap.put("key", "title");
        titleMap.put("value", patient.getTitle());
        titleMap.put("version", 1);
        Map<String, Object> nhsNumberMap = new HashMap<>();
        nhsNumberMap.put("key", "uk.nhs.hospital_number");
        nhsNumberMap.put("value", patient.getNhsNumber());
        nhsNumberMap.put("version", 1);
        // add title and nhs number maps to partyAdditionalInfo
        List<Map<String, Object>> partyAdditionalInfo = new ArrayList<>();
        partyAdditionalInfo.add(titleMap);
        partyAdditionalInfo.add(nhsNumberMap);

        map.put("partyAdditionalInfo", partyAdditionalInfo);

        return map;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
