package com.glinboy.app.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class PusheNotificationProviderServiceImpl
	implements NotificationProviderService<NotificationDTO>, MessageListener {

	private final Logger log = LoggerFactory.getLogger(PusheNotificationProviderServiceImpl.class);
	
	private final ApplicationProperties properties;

	private final JmsTemplate jmsTemplate;

	public PusheNotificationProviderServiceImpl(ApplicationProperties properties, JmsTemplate jmsTemplate) {
		this.properties = properties;
		this.jmsTemplate = jmsTemplate;
	}

	@Override
	public void sendNotification(NotificationDTO notificationDTO) {
		jmsTemplate.convertAndSend("PUSHE_NOTIFICATIONBOX", notificationDTO);
		
	}
	
	private void deliverNotification(NotificationDTO notificationDTO) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
		headers.set("authorization", "Token ".concat(properties.getNotification().getPushe().getToken()));
		JSONObject requestBody = new JSONObject();
		requestBody.put("app_ids", properties.getNotification().getPushe().getAppId());
		JSONObject filters = new JSONObject();
		filters.put("device_id", new JSONArray(List.of(notificationDTO.getToken())));
		requestBody.put("filters", filters);
		JSONObject data = new JSONObject();
		data.put("title", notificationDTO.getSubject());
		data.put("content", notificationDTO.getContent());
		requestBody.put("data", data);
		HttpEntity<String> request = 
				new HttpEntity<>(requestBody.toString(), headers);
		String result = restTemplate.postForObject(properties.getNotification().getPushe().getUrl(),
				request, String.class);
		log.info("Notification sent! {}", notificationDTO);
		log.info("Notification Result {}", result);
	}

	@Override
	@JmsListener(destination = "PUSHE_NOTIFICATIONBOX")
	public void onMessage(Message message) {
		try {
			ObjectMessage objectMessage = (ObjectMessage) message;
			NotificationDTO notificationDTO = (NotificationDTO) objectMessage.getObject();
			deliverNotification(notificationDTO);
		} catch (JMSException e) {
			log.error("Parsing message failed: {}", e.getMessage(), e);
		}
	}

}
