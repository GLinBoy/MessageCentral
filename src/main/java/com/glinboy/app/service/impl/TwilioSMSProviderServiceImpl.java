package com.glinboy.app.service.impl;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.service.ShortMessageProviderService;
import com.glinboy.app.service.dto.ShortMessageDTO;
import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;

@Service
@ConditionalOnProperty(value = "application.sms.provider", havingValue = "twilio", matchIfMissing = true)
public class TwilioSMSProviderServiceImpl implements ShortMessageProviderService<ShortMessageDTO>, MessageListener {

	private final Logger log = LoggerFactory.getLogger(TwilioSMSProviderServiceImpl.class);

	private final ApplicationProperties properties;

	private final JmsTemplate jmsTemplate;

	public TwilioSMSProviderServiceImpl(JmsTemplate jmsTemplate, ApplicationProperties properties) {
		this.jmsTemplate = jmsTemplate;
		this.properties = properties;
	}

	@Override
	public void sendSMS(ShortMessageDTO shortMessageDTO) {
		jmsTemplate.convertAndSend("TWILIO_SMSBOX", shortMessageDTO);
	}

	@Override
	public void sendSMS(List<ShortMessageDTO> shortMessagesDTO) {
		shortMessagesDTO.forEach(s -> jmsTemplate.convertAndSend("TWILIO_SMSBOX", s));
	}

	private void deliverSMS(ShortMessageDTO shortMessageDTO) {
		Twilio.init(properties.getCredential().getTwilio().getAccountSid(),
				properties.getCredential().getTwilio().getToken());
		com.twilio.rest.api.v2010.account.Message message = com.twilio.rest.api.v2010.account.Message
				.creator(new PhoneNumber(shortMessageDTO.getPhoneNumber()),
						new PhoneNumber(properties.getSms().getFrom()), shortMessageDTO.getContent())
				.create();
		log.info("SMS sent! {}", shortMessageDTO);
		log.info("SMS Result {}", message);
	}

	@Override
	@JmsListener(destination = "TWILIO_SMSBOX")
	public void onMessage(Message message) {
		try {
			ObjectMessage objectMessage = (ObjectMessage) message;
			ShortMessageDTO shortMessageDTO = (ShortMessageDTO) objectMessage.getObject();
			deliverSMS(shortMessageDTO);
		} catch (JMSException e) {
			log.error("Parsing message failed: {}", e.getMessage(), e);
		}
	}

}
