package com.glinboy.app.service.impl;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.service.dto.EmailDTO;

@Service
@ConditionalOnProperty(value = "application.email.provider", havingValue = "mail-server", matchIfMissing = true)
public class SmtpMailChannelServiceImpl extends AbstractMailChannelServiceImpl {

    private final JavaMailSender emailSender;

    protected SmtpMailChannelServiceImpl(JmsTemplate jmsTemplate,
            ApplicationProperties properties,
            JavaMailSender emailSender) {
        super(jmsTemplate, properties);
        this.emailSender = emailSender;
    }

    @Override
    public void deliverMessage(EmailDTO... emailsDTO) throws Exception {
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
        log.info("Mail(s) sent! {}", Arrays.toString(emailsDTO));
    }

}
