package com.glinboy.app.service.impl;

import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.service.MailChannelService;
import com.glinboy.app.service.dto.EmailDTO;

public abstract class AbstractMailChannelServiceImpl implements MailChannelService<EmailDTO> {

    public static final String TOPIC_NAME = "MAILBOX";
    
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final ApplicationProperties properties;

    protected final JmsTemplate jmsTemplate;

    protected AbstractMailChannelServiceImpl(JmsTemplate jmsTemplate,
            ApplicationProperties properties) {
        this.jmsTemplate = jmsTemplate;
        this.properties = properties;
    }

    @Override
    public void sendMessage(EmailDTO... emailDTOs) {
        for(var i = 0; i < emailDTOs.length; i++) {
            jmsTemplate.convertAndSend(TOPIC_NAME, emailDTOs[i]);
        }
    }

    @Override
    @JmsListener(destination = AbstractMailChannelServiceImpl.TOPIC_NAME)
    public void onMessage(Message message) {
        try {
            var objectMessage = (ObjectMessage) message;
            EmailDTO emailDTO = (EmailDTO) objectMessage.getObject();
            this.deliverMessage(emailDTO);
        } catch (Exception e) {
            log.error("Parsing message failed: {}", e.getMessage(), e);
        }
    }
}
