package cloud.operon.platform.service.impl;

import cloud.operon.platform.domain.Notification;
import cloud.operon.platform.domain.enumeration.NotificationStatus;
import cloud.operon.platform.repository.NotificationRepository;
import cloud.operon.platform.service.MailService;
import cloud.operon.platform.service.OperinoService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Service Implementation for processing notifications.
 */
@Service
@Transactional
@RabbitListener(queues = "notifications")
@ConfigurationProperties(prefix = "notifier", ignoreUnknownFields = false)
public class NotifactionProcessorImpl {

    private final Logger log = LoggerFactory.getLogger(NotifactionProcessorImpl.class);
    String openEhrUrl;
    String teamName;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private OperinoService operinoService;

    @Autowired
    private MailService mailService;

    @RabbitHandler
    public void receive(@Payload Notification notification) {
        log.info("Received notification {}", notification);

        //build call to open ehr backend
        String plainCreds = operinoService.getConfigForOperino(notification.getOperino()).get("username") +
                ":" + operinoService.getConfigForOperino(notification.getOperino()).get("password");
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> getRequst = new HttpEntity<>(headers);
        log.info("getRequest = " + getRequst);
        try {
            ResponseEntity<Resource> getResponse = restTemplate.exchange(openEhrUrl+notification.getRecordComponentId(), HttpMethod.GET, getRequst, Resource.class);
            log.debug("getResponse = " + getResponse);
            if(getResponse.getStatusCode() == HttpStatus.OK){
                // create input stream form rest call
                InputStream inputStream = getResponse.getBody().getInputStream();
                // now loop though recipients and send emails to all
                notification.getRecipients().forEach(recipient -> {
                    mailService.sendEmail(recipient, notification.getSubject(), notification.getBody(), true, true);
                    mailService.sendEmailWithAttachment(recipient, notification.getSubject(), notification.getBody(),
                            "report.json", inputStream, "application/json", true, true);
                });

                // now loop through confirmation receivers and notify all
                notification.getConfirmationReceivers().forEach(recipient -> {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Your notification was successfully sent to the following recipients:");
                    notification.getRecipients().forEach(r -> {
                        builder.append(r).append("\n");
                    });
                    builder.append("Regards,").append("\n").append(teamName);
                    mailService.sendEmail(recipient, "Delivery confirmation", builder.toString(), false, false);
                });
                // update notification status
                notification.setStatus(NotificationStatus.SENT);

            } else {
                // update notification status
                notification.setStatus(NotificationStatus.FAILED);
                log.error("Unable to verify access composition with id {}. So.", notification.getRecordComponentId());
            }
        } catch (HttpClientErrorException e) {
            // update notification status
            notification.setStatus(NotificationStatus.FAILED);
            log.error("Error accessing openEhrUrl for notification {}. Nested exception is : {}", notification, e);
        } catch (IOException e) {
            // update notification status
            notification.setStatus(NotificationStatus.FAILED);
            log.error("Error reading response from rest call. Nested exception is : ", e);
        }

        //save notification
        notificationRepository.save(notification);
    }

    public void setOpenEhrUrl(String openEhrUrl) {
        this.openEhrUrl = openEhrUrl;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setNotificationRepository(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
}
