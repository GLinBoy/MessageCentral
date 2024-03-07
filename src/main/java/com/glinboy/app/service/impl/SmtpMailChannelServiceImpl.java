package com.glinboy.app.service.impl;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.domain.enumeration.EmailType;
import com.glinboy.app.service.dto.EmailDTO;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "application.email.provider", havingValue = "mail-server", matchIfMissing = true)
public class SmtpMailChannelServiceImpl extends AbstractMailChannelServiceImpl {

    private final JavaMailSender emailSender;

    protected SmtpMailChannelServiceImpl(
        ApplicationEventPublisher publisher,
        ApplicationProperties properties,
        JavaMailSender emailSender
    ) {
        super(publisher, properties);
        this.emailSender = emailSender;
    }

    @Override
    public void deliverMessage(EmailDTO... emailsDTO) throws Exception {
        var messages = new MimeMessage[emailsDTO.length];
        for (var i = 0; i < emailsDTO.length; i++) {
            var emailDTO = emailsDTO[i];
            var message = emailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setFrom(properties.getEmail().getFrom());
            helper.setTo(emailDTO.getReceiver());
            helper.setSubject(emailDTO.getSubject());
            helper.setText(emailDTO.getContent(), EmailType.HTML.equals(emailDTO.getEmailType()));
            messages[i] = message;
        }
        emailSender.send(messages);
        log.info("Mail(s) sent! {}", Arrays.toString(emailsDTO));
    }
}
