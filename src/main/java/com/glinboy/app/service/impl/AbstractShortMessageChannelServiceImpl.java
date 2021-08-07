package com.glinboy.app.service.impl;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.service.ShortMessageChannelService;
import com.glinboy.app.service.ShortMessageService;
import com.glinboy.app.service.dto.ShortMessageDTO;

public abstract class AbstractShortMessageChannelServiceImpl implements ShortMessageChannelService<ShortMessageDTO> {


    public static final String TOPIC_NAME = "SMSBOX";
    
    protected final Logger log;

    protected final ApplicationProperties properties;

    protected final JmsTemplate jmsTemplate;
    
    protected AbstractShortMessageChannelServiceImpl(JmsTemplate jmsTemplate,
            ApplicationProperties properties) {
        log = LoggerFactory.getLogger(this.getClass());
        this.jmsTemplate = jmsTemplate;
        this.properties = properties;
    }

    @Override
    public void sendMessage(ShortMessageDTO... shortMessageDTOs) {
        for(var i = 0; i < shortMessageDTOs.length; i++) {
            jmsTemplate.convertAndSend(TOPIC_NAME, shortMessageDTOs[i]);
        }
    }

    @Override
    @JmsListener(destination = AbstractShortMessageChannelServiceImpl.TOPIC_NAME)
    public void onMessage(ShortMessageDTO... shortMessageDTOs) {
        try {
            deliverMessage(shortMessageDTOs);
        } catch (Exception e) {
            log.error("Parsing message failed: {}", e.getMessage(), e);
            jmsTemplate.convertAndSend(ShortMessageService.TOPIC_NAME_FAILED,
                    Stream.of(shortMessageDTOs).map(ShortMessageDTO::getId).toArray());
        }
        jmsTemplate.convertAndSend(ShortMessageService.TOPIC_NAME_SENT,
                Stream.of(shortMessageDTOs).map(ShortMessageDTO::getId).toArray());
    }
}
