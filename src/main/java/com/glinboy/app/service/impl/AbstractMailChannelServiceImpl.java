package com.glinboy.app.service.impl;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.event.EmailSentFailedEvent;
import com.glinboy.app.event.EmailSentSuccessfulEvent;
import com.glinboy.app.service.MailChannelService;
import com.glinboy.app.service.dto.EmailDTO;

public abstract class AbstractMailChannelServiceImpl implements MailChannelService<EmailDTO> {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final ApplicationProperties properties;

    private final ApplicationEventPublisher publisher;

    protected AbstractMailChannelServiceImpl(ApplicationEventPublisher publisher,
            ApplicationProperties properties) {
        this.publisher = publisher;
        this.properties = properties;
    }

    @Override
    public void sendMessage(EmailDTO... emailDTOs) {
        publisher.publishEvent(emailDTOs);
    }

    @Override
    @EventListener
    public void onMessage(EmailDTO... emailDTO) {
        try {
            this.deliverMessage(emailDTO);
            publisher.publishEvent(new EmailSentSuccessfulEvent(this,
                    Stream.of(emailDTO).map(EmailDTO::getId).collect(Collectors.toList())));
        } catch (Exception e) {
            log.error("An error occurred while sending the message: {}", e.getMessage(), e);
            publisher.publishEvent(new EmailSentFailedEvent(this,
                    Stream.of(emailDTO).map(EmailDTO::getId).collect(Collectors.toList())));
        }
    }
}
