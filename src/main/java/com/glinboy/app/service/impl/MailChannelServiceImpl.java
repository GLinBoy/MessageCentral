package com.glinboy.app.service.impl;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.repository.EmailRepository;
import com.glinboy.app.service.MailChannelService;
import com.glinboy.app.service.dto.EmailDTO;
import com.glinboy.app.service.mapper.EmailMapper;

@Service
@ConditionalOnProperty(value = "application.email.provider", havingValue = "mail-server", matchIfMissing = true)
public class MailChannelServiceImpl extends GenericChannelServiceImpl<EmailDTO>
        implements MailChannelService<EmailDTO> {

    public static final String TOPIC_NAME = "MAILBOX";

    private final JavaMailSender emailSender;

    private final EmailRepository emailRepository;

    private final EmailMapper emailMapper;

    private final ApplicationContext applicationContext;

    public MailChannelServiceImpl(JmsTemplate jmsTemplate,
            ApplicationProperties properties,
            JavaMailSender emailSender,
            EmailRepository emailRepository,
            EmailMapper emailMapper,
            ApplicationContext applicationContext) {
        super(jmsTemplate, properties);
        this.emailSender = emailSender;
        this.emailRepository = emailRepository;
        this.emailMapper = emailMapper;
        this.applicationContext = applicationContext;
    }

    @Override
    public String getTopicName() {
        return TOPIC_NAME;
    }
    
    @Override
    public Consumer<EmailDTO[]> saveFunction() {
        return emails -> emailRepository.saveAll(Stream
                .of(emails)
                .map(emailMapper::toEntity)
                .collect(Collectors.toList()));
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
        updateStatusToSent(emailsDTO);
        log.info("Mail(s) sent! {}", Arrays.toString(emailsDTO));
    }

    @Override
    @Transactional
    @JmsListener(destination = MailChannelServiceImpl.TOPIC_NAME)
    public void onMessage(Message message) {
        try {
            var objectMessage = (ObjectMessage) message;
            var emailDTO = (EmailDTO) objectMessage.getObject();
            ((MailChannelServiceImpl) applicationContext.getBean(MailChannelServiceImpl.class)).deliverMessage(emailDTO);
        } catch (JMSException e) {
            log.error("Parsing message failed: {}", e.getMessage(), e);
        }
    }

}
