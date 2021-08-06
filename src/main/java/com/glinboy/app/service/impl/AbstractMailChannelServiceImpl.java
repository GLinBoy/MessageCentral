package com.glinboy.app.service.impl;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.service.EmailService;
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
    public void onMessage(EmailDTO... emailDTO) {
        try {
            this.deliverMessage(emailDTO);
        } catch (Exception e) {
            log.error("An error occurred while sending the message: {}", e.getMessage(), e);
            jmsTemplate.convertAndSend(EmailService.TOPIC_NAME_FAILED,
                    Stream.of(emailDTO).map(EmailDTO::getId).toArray());
        }
        jmsTemplate.convertAndSend(EmailService.TOPIC_NAME_SENT,
                Stream.of(emailDTO).map(EmailDTO::getId).toArray());
    }
}
