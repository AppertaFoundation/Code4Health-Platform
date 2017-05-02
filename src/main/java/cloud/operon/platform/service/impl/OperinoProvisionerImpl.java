package cloud.operon.platform.service.impl;

import cloud.operon.platform.domain.Operino;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Service Implementation for provisioning Operinos.
 */
@Service
@Transactional
@RabbitListener(queues = "operinos")
@ConfigurationProperties(prefix = "provisioner", ignoreUnknownFields = false)
public class OperinoProvisionerImpl {

    private final Logger log = LoggerFactory.getLogger(OperinoProvisionerImpl.class);
//    private String url = "http://explorer.termlex.com/ehrscape-manager/rest/domain";
    String url;
    String username;
    String password;

    @Autowired
    public RestTemplate restTemplate;

    public OperinoProvisionerImpl() {
    }

    @RabbitHandler
    public void receive(@Payload Operino operino) throws JsonProcessingException {
        log.info("Received operino {}", operino);
        // now build variables for posting to ehrscape provisioner
        String plainCreds = username + ":" + password;
        log.info("plainCreds = " + plainCreds);
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        headers.add("Content-Type", "application/json");

        String name = operino.getUser().getFirstName().concat(operino.getUser().getLastName());
        if(name.length() < 1){
            name = operino.getDomain();
        }
        // create Map of data to be posted for domain creation
        Map<String, String> data= new HashMap<>();
        data.put("domainName", operino.getDomain());
//        data.put("domainName", "xxxxxxxxx");
        data.put("domainSystemId", operino.getDomain());
//        data.put("domainSystemId", "yyyyyyyyyy");
        data.put("name", name);
        data.put("username", operino.getUser().getLogin()+"_"+operino.getDomain());
//        data.put("password", operino.getUser().getPassword());
        data.put("password", "mmoooooossssss");

        // post data to api
        HttpEntity<Map<String, String>> getRequst = new HttpEntity<>(headers);
        log.info("getRequest = " + getRequst);
//        int statusCode = restTemplate.exchange(getDomainUrl+operino.getDomain(), HttpMethod.GET, getRequst, Object.class).getStatusCodeValue();
        int statusCode = restTemplate.exchange("http://explorer.termlex.com/ehrscape-manager/rest/domain", HttpMethod.GET, getRequst, Object.class).getStatusCodeValue();
        log.info("statusCode = " + statusCode);
//        log.info("verifyResponse = " + verifyResponse);
//        log.info("verifyResponse.getStatusCodeValue() = " + verifyResponse.getStatusCodeValue());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(data, headers);
        log.info("request = " + request);
        ResponseEntity response = restTemplate.postForEntity(url, request, HttpEntity.class);
//        restTemplate.postForLocation(URI.create(url), request);
        log.info("response = " + response);

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
