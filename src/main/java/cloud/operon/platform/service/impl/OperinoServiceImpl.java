package cloud.operon.platform.service.impl;

import cloud.operon.platform.domain.Notification;
import cloud.operon.platform.domain.Operino;
import cloud.operon.platform.domain.enumeration.NotificationStatus;
import cloud.operon.platform.repository.NotificationRepository;
import cloud.operon.platform.repository.OperinoRepository;
import cloud.operon.platform.repository.search.OperinoSearchRepository;
import cloud.operon.platform.security.SecurityUtils;
import cloud.operon.platform.service.OperinoService;
import cloud.operon.platform.service.UserService;
import cloud.operon.platform.service.util.ThinkEhrRestClient;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing Operino.
 */
@Service
@Transactional
public class OperinoServiceImpl implements OperinoService{

    private final Logger log = LoggerFactory.getLogger(OperinoServiceImpl.class);
    
    private final OperinoRepository operinoRepository;
    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final OperinoSearchRepository operinoSearchRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ThinkEhrRestClient thinkEhrRestClient;

    public OperinoServiceImpl(OperinoRepository operinoRepository,
                              NotificationRepository notificationRepository,
                              OperinoSearchRepository operinoSearchRepository,
                              UserService userService,
                              RabbitTemplate rabbitTemplate,
                              ThinkEhrRestClient thinkEhrRestClient) {
        this.operinoRepository = operinoRepository;
        this.operinoSearchRepository = operinoSearchRepository;
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.rabbitTemplate = rabbitTemplate;
        this.thinkEhrRestClient = thinkEhrRestClient;
    }

    /**
     * Save a operino.
     *
     * @param operino the entity to save
     * @return the persisted entity
     */
    @Override
    public Operino save(Operino operino) {
        log.debug("Request to save Operino : {}", operino);
        operino.setUser(userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin()).get());
        Operino result = operinoRepository.save(operino);
        operinoSearchRepository.save(result);
        rabbitTemplate.convertAndSend("operinos", result);
        log.info("Sent off result to rabbitmq");
        return result;
    }

    /**
     *  Get all the operinos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Operino> findAll(Pageable pageable) {
        log.debug("Request to get all Operinos");
        if (userService.isAdmin()) {
            Page<Operino> result = operinoRepository.findAll(pageable);
            return result;
        } else {
            Page<Operino> result = operinoRepository.findByUserIsCurrentUser(SecurityUtils.getCurrentUserLogin(), pageable);
            return result;
        }
    }

    /**
     *  Get one operino by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Operino verifyOwnershipAndGet(Long id) {
        log.debug("Request to verify ownership and get Operino : {}", id);
        Operino operino = operinoRepository.findOneByUserAndId(SecurityUtils.getCurrentUserLogin(), id);
        if(operino != null){
            return operino;
        } else if(userService.isAdmin()){
            return operinoRepository.findOne(id);
        } else {
            return null;
        }
    }


    /**
     *  Get one operino by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Operino findOne(Long id) {
        log.debug("Request to get Operino : {}", id);
        return this.verifyOwnershipAndGet(id);
    }

    /**
     *  Delete the  operino by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Operino : {}", id);
        // veirfy ownership
        Operino operino = findOne(id);
        if(operino != null) {
            // first truncate domain
            thinkEhrRestClient.truncateDomain(operino.getDomain());
            operinoRepository.delete(id);
            operinoSearchRepository.delete(id);
        } else {
            log.error("Unable to find operino {} to delete", id);
        }
    }

    /**
     * Search for the operino corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Operino> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Operinos for query {}", query);
        Page<Operino> result = operinoSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }


    /**
     * Gets config associated with an operino
     * @param operino the operino to get config for
     * @return the congig as a map
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getConfigForOperino(Operino operino) {

        String name = operino.getUser().getFirstName() + operino.getUser().getLastName();
        if(name.length() < 1){
            name = operino.getDomain();
        }

        // create basic auth token
        String operinoUserName = operino.getUser().getLogin()+"_"+operino.getDomain();
        String operinoPassword = operino.getUser().getPassword().substring(0, 12);
        String plainCreds = operinoUserName + ":" + operinoPassword;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        // create Map of data to be posted for domain creation
        Map<String, String> data= new HashMap<>();
        data.put("domainName", operino.getDomain());
        data.put("domainSystemId", operino.getDomain());
        data.put("name", name);
        data.put("username", operinoUserName);
        data.put("password", operinoPassword);
        data.put("token", base64Creds);

        return data;
    }

    @Override
    public Notification sendNotification(Notification notification) {
        // save notification
        notification.setStatus(NotificationStatus.INPROGRESS);
//        notification = notificationRepository.save(notification);
        rabbitTemplate.convertAndSend("notifications", notification);
        log.info("Notification sent to rabbitmq");

        return notification;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getNotifications(Pageable pageable) {
        if (userService.isAdmin()) {
            Page<Notification> result = notificationRepository.findAll(pageable);
            return result;
        } else {
            Page<Notification> result = notificationRepository.findByUserIsCurrentUser(SecurityUtils.getCurrentUserLogin(), pageable);
            return result;
        }
    }

}
