package com.glinboy.app.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.service.ShortMessageChannelService;
import com.glinboy.app.service.dto.ShortMessageDTO;
import com.kavenegar.sdk.KavenegarApi;
import com.kavenegar.sdk.excepctions.ApiException;
import com.kavenegar.sdk.excepctions.HttpException;
import com.kavenegar.sdk.models.SendResult;

@Service
@ConditionalOnProperty(value = "application.sms.provider", havingValue = "kavenegar")
public class KavenegarSMSChannelServiceImpl extends GenericChannelServiceImpl<ShortMessageDTO> implements ShortMessageChannelService<ShortMessageDTO> {
	
	public static final String TOPIC_NAME = "KAVENEGAR_SMSBOX";

	protected KavenegarSMSChannelServiceImpl(JmsTemplate jmsTemplate,
			ApplicationProperties properties) {
		super(jmsTemplate, properties);
	}

	@Override
	String getTopicName() {
		return TOPIC_NAME;
	}

	@Override
	public void deliverMessage(ShortMessageDTO... shortMessageDTOs) {
		var api = new KavenegarApi(properties.getCredential().getKavenegar().getToken());
		for (var i = 0; i < shortMessageDTOs.length; i++) {
			try {
				SendResult result = api.send(properties.getSms().getFrom(),
						shortMessageDTOs[i].getPhoneNumber(), shortMessageDTOs[i].getContent());
				log.info("SMS sent! {}", shortMessageDTOs[i]);
				log.info("SMS Result {}", result);
			} catch (HttpException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
				log.error("HttpException: {}", ex.getMessage(), ex);
			} catch (ApiException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
				log.error("ApiException: {}", ex.getMessage(), ex);
			}
		}
	}

	@Override
	@JmsListener(destination = KavenegarSMSChannelServiceImpl.TOPIC_NAME)
	public void onMessage(Message message) {
		try {
			var objectMessage = (ObjectMessage) message;
			var shortMessageDTO = (ShortMessageDTO) objectMessage.getObject();
			this.deliverMessage(shortMessageDTO);
		} catch (JMSException e) {
			log.error("Parsing message failed: {}", e.getMessage(), e);
		}
	}

}
