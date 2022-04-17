package com.glinboy.app.service.impl;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.event.ShortMessageSentFailedEvent;
import com.glinboy.app.event.ShortMessageSentSuccessfulEvent;
import com.glinboy.app.service.ShortMessageChannelService;
import com.glinboy.app.service.dto.ShortMessageDTO;

public abstract class AbstractShortMessageChannelServiceImpl implements ShortMessageChannelService<ShortMessageDTO> {


    protected final Logger log;

    protected final ApplicationProperties properties;

    private final ApplicationEventPublisher publisher;
    
    protected AbstractShortMessageChannelServiceImpl(ApplicationEventPublisher publisher,
            ApplicationProperties properties) {
        log = LoggerFactory.getLogger(this.getClass());
        this.publisher = publisher;
        this.properties = properties;
    }

    @Override
    public void sendMessage(ShortMessageDTO... shortMessageDTOs) {
        publisher.publishEvent(shortMessageDTOs);
    }

    @Override
    @EventListener
    public void onMessage(ShortMessageDTO... shortMessageDTOs) {
        try {
            deliverMessage(shortMessageDTOs);
            publisher.publishEvent(new ShortMessageSentSuccessfulEvent(this,
                    Stream.of(shortMessageDTOs).map(ShortMessageDTO::getId).collect(Collectors.toList())));
        } catch (Exception e) {
            log.error("Parsing message failed: {}", e.getMessage(), e);
            jmsTemplate.convertAndSend(ShortMessageService.TOPIC_NAME_FAILED,
                    Stream.of(shortMessageDTOs).map(ShortMessageDTO::getId).toArray());
        }
    }
}
