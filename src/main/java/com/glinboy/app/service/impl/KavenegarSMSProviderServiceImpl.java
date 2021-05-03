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
import com.kavenegar.sdk.KavenegarApi;
import com.kavenegar.sdk.excepctions.ApiException;
import com.kavenegar.sdk.excepctions.HttpException;
import com.kavenegar.sdk.models.SendResult;

@Service
@ConditionalOnProperty(value = "application.sms.provider", havingValue = "kavenegar")
public class KavenegarSMSProviderServiceImpl implements ShortMessageProviderService<ShortMessageDTO>, MessageListener {

	private final Logger log = LoggerFactory.getLogger(KavenegarSMSProviderServiceImpl.class);

	private final ApplicationProperties properties;

	private final JmsTemplate jmsTemplate;

	public KavenegarSMSProviderServiceImpl(JmsTemplate jmsTemplate, ApplicationProperties properties) {
		this.jmsTemplate = jmsTemplate;
		this.properties = properties;
	}

	@Override
	public void sendSMS(ShortMessageDTO shortMessageDTO) {
		jmsTemplate.convertAndSend("KAVENEGAR_SMSBOX", shortMessageDTO);
	}
	
	@Override
	public void sendSMS(List<ShortMessageDTO> shortMessagesDTO) {
		shortMessagesDTO.forEach(s -> jmsTemplate.convertAndSend("KAVENEGAR_SMSBOX", s));
	}

	private void deliverSMS(ShortMessageDTO shortMessageDTO) {
		try {
			KavenegarApi api = new KavenegarApi(properties.getCredential().getKavenegar().getToken());
			SendResult result = api.send(properties.getSms().getFrom(),
					shortMessageDTO.getPhoneNumber(), shortMessageDTO.getContent());
			log.info("SMS sent! {}", shortMessageDTO);
			log.info("SMS Result {}", result);
		} catch (HttpException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
			log.error("HttpException: {}", ex.getMessage(), ex);
		} catch (ApiException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
			log.error("ApiException: {}", ex.getMessage(), ex);
		}
	}

	@Override
	@JmsListener(destination = "KAVENEGAR_SMSBOX")
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
