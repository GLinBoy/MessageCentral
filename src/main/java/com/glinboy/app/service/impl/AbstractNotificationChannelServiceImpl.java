package com.glinboy.app.service.impl;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.event.NotificationSentFailedEvent;
import com.glinboy.app.event.NotificationSentSuccessfulEvent;
import com.glinboy.app.service.NotificationChannelService;
import com.glinboy.app.service.dto.NotificationDTO;

public abstract class AbstractNotificationChannelServiceImpl implements NotificationChannelService<NotificationDTO> {


    protected final Logger log;

    protected final ApplicationProperties properties;

    private final ApplicationEventPublisher publisher;
    
    protected AbstractNotificationChannelServiceImpl(ApplicationEventPublisher publisher,
            ApplicationProperties properties) {
        log = LoggerFactory.getLogger(this.getClass());
        this.publisher = publisher;
        this.properties = properties;
    }

    @Override
    public void sendMessage(NotificationDTO... notificationDTOs) {
        publisher.publishEvent(notificationDTOs);
    }

    @Override
    @EventListener
    public void onMessage(NotificationDTO... notificationDTOs) {
        try {
            deliverMessage(notificationDTOs);
            jmsTemplate.convertAndSend(NotificationService.TOPIC_NAME_SENT,
                    Stream.of(notificationDTOs).map(NotificationDTO::getId).toArray());
        } catch (Exception e) {
            log.error("Parsing message failed: {}", e.getMessage(), e);
            jmsTemplate.convertAndSend(NotificationService.TOPIC_NAME_FAILED,
                    Stream.of(notificationDTOs).map(NotificationDTO::getId).toArray());
        }
    }
}
