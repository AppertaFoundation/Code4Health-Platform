package cloud.operon.platform.service.impl;

import cloud.operon.platform.domain.Operino;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for provisioning Operinos.
 */
@Service
@Transactional
@RabbitListener(queues = "operinos")
public class OperinoProvisionerImpl {

    private final Logger log = LoggerFactory.getLogger(OperinoProvisionerImpl.class);

    public OperinoProvisionerImpl() {
    }

    @RabbitHandler
    public void receive(@Payload Operino operino) {
        log.info("Received operino {}", operino);
    }
}
