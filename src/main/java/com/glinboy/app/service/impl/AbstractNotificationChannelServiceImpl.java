package com.glinboy.app.service.impl;

import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.service.NotificationChannelService;
import com.glinboy.app.service.dto.NotificationDTO;

public abstract class AbstractNotificationChannelServiceImpl implements NotificationChannelService<NotificationDTO> {


    public static final String TOPIC_NAME = "NOTIFICATIONBOX";
    
    protected final Logger log;

    protected final ApplicationProperties properties;

    protected final JmsTemplate jmsTemplate;
    
    protected AbstractNotificationChannelServiceImpl(JmsTemplate jmsTemplate,
            ApplicationProperties properties) {
        log = LoggerFactory.getLogger(this.getClass());
        this.jmsTemplate = jmsTemplate;
        this.properties = properties;
    }

    @Override
    public void sendMessage(NotificationDTO... notificationDTOs) {
        for(var i = 0; i < notificationDTOs.length; i++) {
            jmsTemplate.convertAndSend(TOPIC_NAME, notificationDTOs[i]);
        }
    }

    @Override
    @JmsListener(destination = AbstractNotificationChannelServiceImpl.TOPIC_NAME)
    public void onMessage(Message message) {
        try {
            var objectMessage = (ObjectMessage) message;
            var notificationDTO = (NotificationDTO) objectMessage.getObject();
            deliverMessage(notificationDTO);
        } catch (Exception e) {
            log.error("Parsing message failed: {}", e.getMessage(), e);
        }
    }
}
