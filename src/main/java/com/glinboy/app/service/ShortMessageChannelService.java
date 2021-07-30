package com.glinboy.app.service;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.service.dto.ShortMessageDTO;

public abstract class ShortMessageChannelService implements ChannelService<ShortMessageDTO>, MessageListener {


    public static final String TOPIC_NAME = "TWILIO_SMSBOX";
    
    protected final Logger log;

    protected final ApplicationProperties properties;

    protected final JmsTemplate jmsTemplate;
    
    protected ShortMessageChannelService(JmsTemplate jmsTemplate,
            ApplicationProperties properties) {
        log = LoggerFactory.getLogger(this.getClass());
        this.jmsTemplate = jmsTemplate;
        this.properties = properties;
    }

    public abstract void deliverMessage(ShortMessageDTO... shortMessageDTOs) throws Exception;

    @Override
    public void sendMessage(ShortMessageDTO... shortMessageDTOs) {
        for(var i = 0; i < shortMessageDTOs.length; i++) {
            jmsTemplate.convertAndSend(TOPIC_NAME, shortMessageDTOs[i]);
        }
    }

    @Override
    @JmsListener(destination = ShortMessageChannelService.TOPIC_NAME)
    public void onMessage(Message message) {
        try {
            var objectMessage = (ObjectMessage) message;
            var shortMessageDTO = (ShortMessageDTO) objectMessage.getObject();
            deliverMessage(shortMessageDTO);
        } catch (Exception e) {
            log.error("Parsing message failed: {}", e.getMessage(), e);
        }
    }
}
