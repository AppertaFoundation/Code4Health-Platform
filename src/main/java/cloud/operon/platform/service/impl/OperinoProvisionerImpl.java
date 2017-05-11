package cloud.operon.platform.service.impl;

import cloud.operon.platform.domain.Operino;
import cloud.operon.platform.service.OperinoService;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Service Implementation for provisioning Operinos.
 */
@Service
@Transactional
@RabbitListener(queues = "operinos")
@ConfigurationProperties(prefix = "provisioner", ignoreUnknownFields = false)
public class OperinoProvisionerImpl implements InitializingBean, cloud.operon.platform.service.OperinoProvisioner {

    private final Logger log = LoggerFactory.getLogger(OperinoProvisionerImpl.class);
    String domainUrl;
    String templateUrl;
    String username;
    String password;
    String templateToSubmit;

    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    public OperinoService operinoService;

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
            log.info("getResponse = " + getResponse);
            if(getResponse.getStatusCode() == HttpStatus.OK && !getResponse.getBody().contains(operino.getDomain())){

                HttpEntity<Map<String, String>> domainRequest = new HttpEntity<>(data, headers);
                log.info("domainRequest = " + domainRequest);
                ResponseEntity<String> domainResponse = restTemplate.postForEntity(domainUrl, domainRequest, String.class);
                log.info("domainResponse = " + domainResponse);
                if(domainResponse.getStatusCode() == HttpStatus.CREATED) {
                    // create template headers for xml data post
                    HttpHeaders templateHeaders = new HttpHeaders();
                    templateHeaders.setContentType(MediaType.APPLICATION_XML);
                    templateHeaders.add("Authorization", "Basic " + token);
                    HttpEntity<String> templateRequest = new HttpEntity<>(templateToSubmit, templateHeaders);
                    log.info("templateRequest = " + templateRequest);
                    ResponseEntity templateResponse = restTemplate.postForEntity(templateUrl, templateRequest, String.class);
                    log.info("templateResponse = " + templateResponse);
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
        HttpEntity<Map<String, String>> getRequst = new HttpEntity<>(headers);
        log.info("getRequest = " + getRequst);
        ResponseEntity<List> getResponse;
        try {
            getResponse = restTemplate.exchange(domainUrl, HttpMethod.GET, getRequst, List.class);
        }
        catch (HttpClientErrorException e) {
            throw new RuntimeException("Unable to connect to ThinkEHR backend specified by: " + domainUrl);
        }

        log.info("getResponse = " + getResponse);
        if(getResponse == null || getResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Unable to connect to ThinkEHR backend specified by: " + domainUrl);
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

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    public String getDomainUrl() {
        return domainUrl;
    }

    public void setDomainUrl(String domainUrl) {
        this.domainUrl = domainUrl;
    }
}
