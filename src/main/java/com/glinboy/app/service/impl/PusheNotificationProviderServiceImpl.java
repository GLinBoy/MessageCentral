package com.glinboy.app.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.service.NotificationProviderService;
import com.glinboy.app.service.dto.NotificationDTO;

@Service
@ConditionalOnProperty(value = "application.notification.provider", havingValue = "pushe")
public class PusheNotificationProviderServiceImpl extends GenericChannelServiceImpl<NotificationDTO>
	implements NotificationProviderService<NotificationDTO> {
	
	public static final String TOPIC_NAME = "PUSHE_NOTIFICATIONBOX";

	protected PusheNotificationProviderServiceImpl(JmsTemplate jmsTemplate,
			ApplicationProperties properties) {
		super(jmsTemplate, properties);
	}

	@Override
	String getTopicName() {
		return TOPIC_NAME;
	}

	@Override
	public void deliverMessage(NotificationDTO... notificationDTOs) {
		for (var i = 0; i < notificationDTOs.length; i++) {
			var restTemplate = new RestTemplate();
			var headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
			headers.set("authorization", "Token ".concat(properties.getNotification().getPushe().getToken()));
			var requestBody = new JSONObject();
			requestBody.put("app_ids", properties.getNotification().getPushe().getAppId());
			var filters = new JSONObject();
			filters.put("device_id", new JSONArray(List.of(notificationDTOs[i].getToken())));
			requestBody.put("filters", filters);
			var data = new JSONObject();
			data.put("title", notificationDTOs[i].getSubject());
			data.put("content", notificationDTOs[i].getContent());
			requestBody.put("data", data);
			HttpEntity<String> request = 
					new HttpEntity<>(requestBody.toString(), headers);
			String result = restTemplate.postForObject(properties.getNotification().getPushe().getUrl(),
					request, String.class);
			log.info("Notification sent! {}", notificationDTOs[i]);
			log.info("Notification Result {}", result);
		}
	}

	@Override
	@JmsListener(destination = "PUSHE_NOTIFICATIONBOX")
	public void onMessage(Message message) {
		try {
			var objectMessage = (ObjectMessage) message;
			var notificationDTO = (NotificationDTO) objectMessage.getObject();
			this.deliverMessage(notificationDTO);
		} catch (JMSException e) {
			log.error("Parsing message failed: {}", e.getMessage(), e);
		}
	}

}
