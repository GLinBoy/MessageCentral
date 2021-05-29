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
import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;

@Service
@ConditionalOnProperty(value = "application.sms.provider", havingValue = "twilio", matchIfMissing = true)
public class TwilioSMSChannelServiceImpl extends GenericChannelServiceImpl<ShortMessageDTO> implements ShortMessageChannelService<ShortMessageDTO> {
	
	public static final String TOPIC_NAME = "TWILIO_SMSBOX";

	protected TwilioSMSChannelServiceImpl(JmsTemplate jmsTemplate,
			ApplicationProperties properties) {
		super(jmsTemplate, properties);
	}

	@Override
	String getTopicName() {
		return TOPIC_NAME;
	}

	@Override
	public void deliverMessage(ShortMessageDTO... shortMessageDTOs) {
		Twilio.init(properties.getCredential().getTwilio().getAccountSid(),
				properties.getCredential().getTwilio().getToken());
		for (var i = 0; i < shortMessageDTOs.length; i++) {
			var message = com.twilio.rest.api.v2010.account.Message
					.creator(new PhoneNumber(shortMessageDTOs[i].getPhoneNumber()),
							new PhoneNumber(properties.getSms().getFrom()), shortMessageDTOs[i].getContent())
					.create();
			log.info("SMS sent! {}", shortMessageDTOs[i]);
			log.info("SMS Result {}", message);
		}
	}

	@Override
	@JmsListener(destination = TwilioSMSChannelServiceImpl.TOPIC_NAME)
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
