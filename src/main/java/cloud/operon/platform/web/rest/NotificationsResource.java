package cloud.operon.platform.web.rest;

import cloud.operon.platform.domain.Notification;
import cloud.operon.platform.domain.Operino;
import cloud.operon.platform.service.OperinoService;
import cloud.operon.platform.web.rest.util.HeaderUtil;
import cloud.operon.platform.web.rest.util.PaginationUtil;
import com.codahale.metrics.annotation.Timed;
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

/**
 * REST controller for managing notifications.
 */
@RestController
@RequestMapping("/api")
public class NotificationsResource {

    private final Logger log = LoggerFactory.getLogger(NotificationsResource.class);

    private static final String ENTITY_NAME = "operino";
    private static final String NOTIFICATION_ENTITY_NAME = "notification";
    private final boolean skipOperinoValidation = true;
    private final OperinoService operinoService;

    public NotificationsResource(OperinoService operinoService) {
        this.operinoService = operinoService;
    }

    /**
     * GET  /operinos/:id/notifications : get the notifications linked to the "id" operino.
     *
     * @param id the id of the operino to retrieve notifications for
     * @return the ResponseEntity with status 200 (OK) and with body list of notifications, or with status 404 (Not Found)
     */
    @GetMapping("/operinos/{id}/notifications")
    @Timed
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long id, Pageable pageable) throws URISyntaxException {
        log.debug("REST request to get components for Operino : {}", id);
        Operino operino = operinoService.verifyOwnershipAndGet(id);
        if (operino != null) {
            Page<Notification> page = operinoService.getNotifications(pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/notifications");
            return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        } else {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "Not authorized", String.valueOf(id))).build();
        }
    }

    /**
     * POST  /notifications/:id : send composition as notification for the "id" operino.
     *
     * @param id the id of the operino that contains the composition id
     * @return the ResponseEntity with status 201 (OK) and with body the notification, or with status 404 (Not Found)
     */
    @PostMapping("/notifications/{id}")
    @Timed
    public ResponseEntity<Notification> sendNotification(@PathVariable Long id, @Valid @RequestBody Notification notification) throws URISyntaxException {
        log.debug("REST request to send notification for Operino : {} ", id);
        Operino operino = operinoService.verifyOwnershipAndGet(id);
        if (skipOperinoValidation || operino != null) {
            notification.setOperino(operino);
            // now send notification using operino service and return call
            Notification result = operinoService.sendNotification(notification);
            return ResponseEntity.created(new URI("/api/notifications/"))
                    .headers(HeaderUtil.createEntityCreationAlert(NOTIFICATION_ENTITY_NAME, "xxxxx"))
                    .body(result);
        } else {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "Not found", String.valueOf(id))).build();
        }
    }

}
