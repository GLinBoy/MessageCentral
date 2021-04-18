package com.glinboy.app.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.glinboy.app.service.MailProviderService;
import com.glinboy.app.service.dto.EmailDTO;

@Service
public class MailProviderServiceImpl implements MailProviderService<EmailDTO> {

	private final Logger log = LoggerFactory.getLogger(MailProviderServiceImpl.class);
	
	private final JavaMailSender emailSender;

	public MailProviderServiceImpl(JavaMailSender emailSender) {
		this.emailSender = emailSender;
	}

	@Override
	public void sendEmail(EmailDTO emailDTO) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("noreply@baeldung.com");
		message.setTo(emailDTO.getReceiver());
		message.setSubject(emailDTO.getSubject());
		message.setText(emailDTO.getContent());
		emailSender.send(message);
		log.info("Mail sended! {}", emailDTO);
	}

}
