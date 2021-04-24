package com.glinboy.app.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.service.MailProviderService;
import com.glinboy.app.service.dto.EmailDTO;

@Service
@ConditionalOnProperty(value = "application.email.provider", havingValue = "mail-server", matchIfMissing = true)
public class MailProviderServiceImpl implements MailProviderService<EmailDTO>, MessageListener {

	private final Logger log = LoggerFactory.getLogger(MailProviderServiceImpl.class);
	
	private final ApplicationProperties properties;
	
	private final JavaMailSender emailSender;
	
	private final JmsTemplate jmsTemplate;

	public MailProviderServiceImpl(JavaMailSender emailSender,
			JmsTemplate jmsTemplate,
			ApplicationProperties properties) {
		this.emailSender = emailSender;
		this.jmsTemplate = jmsTemplate;
		this.properties = properties;
	}

	@Override
	public void sendEmail(EmailDTO emailDTO) {
		jmsTemplate.convertAndSend("MAILBOX", emailDTO);
	}
	
	private void deliverEmail(EmailDTO emailDTO) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(properties.getEmail().getFrom());
		message.setTo(emailDTO.getReceiver());
		message.setSubject(emailDTO.getSubject());
		message.setText(emailDTO.getContent());
		emailSender.send(message);
		log.info("Mail sent! {}", emailDTO);
	}

	@Override
	@JmsListener(destination = "MAILBOX")
	public void onMessage(Message message) {
		try {
			ObjectMessage objectMessage = (ObjectMessage)message;
			EmailDTO emailDTO = (EmailDTO)objectMessage.getObject();
			deliverEmail(emailDTO);
		} catch (JMSException e) {
			log.error("Parsing message failed: {}", e.getMessage(), e);
		}
	}

}
