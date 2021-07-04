package com.glinboy.app.service.impl;

import java.util.Arrays;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.domain.enumeration.MessageStatus;
import com.glinboy.app.service.EmailService;
import com.glinboy.app.service.MailChannelService;
import com.glinboy.app.service.dto.EmailDTO;

@Service
@ConditionalOnProperty(value = "application.email.provider", havingValue = "mail-server", matchIfMissing = true)
public class MailChannelServiceImpl extends GenericChannelServiceImpl<EmailDTO>
        implements MailChannelService<EmailDTO> {

    public static final String TOPIC_NAME = "MAILBOX";

    private final JavaMailSender emailSender;

    private final EmailService emailService;

    public MailChannelServiceImpl(JmsTemplate jmsTemplate,
            ApplicationProperties properties,
            JavaMailSender emailSender,
            EmailService emailService) {
        super(jmsTemplate, properties);
        this.emailSender = emailSender;
        this.emailService = emailService;
    }

    @Override
    String getTopicName() {
        return TOPIC_NAME;
    }

    @Override
    public void deliverMessage(EmailDTO... emailsDTO) {
        var messages = new SimpleMailMessage[emailsDTO.length];
        for (var i = 0; i < emailsDTO.length; i++) {
            var emailDTO = emailsDTO[i];
            var message = new SimpleMailMessage();
            message.setFrom(properties.getEmail().getFrom());
            message.setTo(emailDTO.getReceiver());
            message.setSubject(emailDTO.getSubject());
            message.setText(emailDTO.getContent());
            messages[i] = message;
        }
        emailSender.send(messages);
        for(int i = 0; i < emailsDTO.length; i++) {
            var emailDTO = emailsDTO[i];
            emailDTO.setStatus(MessageStatus.SENT);
            this.emailService.partialUpdate(emailDTO);
        }
        log.info("Mail(s) sent! {}", Arrays.toString(emailsDTO));
    }

    @Override
    @JmsListener(destination = MailChannelServiceImpl.TOPIC_NAME)
    public void onMessage(Message message) {
        try {
            var objectMessage = (ObjectMessage) message;
            var emailDTO = (EmailDTO) objectMessage.getObject();
            this.deliverMessage(emailDTO);
        } catch (JMSException e) {
            log.error("Parsing message failed: {}", e.getMessage(), e);
        }
    }

}
