package com.glinboy.app.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.collections.CollectionUtils;
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
import com.glinboy.app.service.dto.NotificationDataDTO;

@Service
@ConditionalOnProperty(value = "application.notification.provider", havingValue = "onesignal", matchIfMissing = true)
public class OnesignalNotificationProviderServiceImpl
	implements NotificationProviderService<NotificationDTO>, MessageListener {

	private final Logger log = LoggerFactory.getLogger(OnesignalNotificationProviderServiceImpl.class);
	
	private final ApplicationProperties properties;

	private final JmsTemplate jmsTemplate;

	public OnesignalNotificationProviderServiceImpl(ApplicationProperties properties,
			JmsTemplate jmsTemplate) {
		this.properties = properties;
		this.jmsTemplate = jmsTemplate;
	}

	@Override
	public void sendNotification(NotificationDTO notificationDTO) {
		jmsTemplate.convertAndSend("ONESIGNAL_NOTIFICATIONBOX", notificationDTO);
	}

	@Override
	public void sendNotification(List<NotificationDTO> notificationDTOs) {
		notificationDTOs.forEach(n -> jmsTemplate.convertAndSend("ONESIGNAL_NOTIFICATIONBOX", n));
	}
	
	private void deliverNotification(NotificationDTO notificationDTO) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
		JSONObject requestBody = new JSONObject();
		requestBody.put("app_id", properties.getNotification().getOnesignal().getAppId());
		requestBody.put("include_player_ids", new JSONArray(List.of(notificationDTO.getToken())));
		JSONObject headings = new JSONObject();
		headings.put("en", notificationDTO.getSubject());
		requestBody.put("headings", headings);
		JSONObject contents = new JSONObject();
		contents.put("en", notificationDTO.getContent());
		requestBody.put("contents", contents);
		if(!CollectionUtils.isEmpty(notificationDTO.getData())) {
			requestBody.put("data", notificationDTO.getData().stream()
					.collect(Collectors.toMap(NotificationDataDTO::getKey,
							NotificationDataDTO::getValue)));
		}
		HttpEntity<String> request = 
				new HttpEntity<>(requestBody.toString(), headers);
		String result = restTemplate.postForObject(properties.getNotification().getOnesignal().getUrl(),
				request, String.class);
		log.info("Notification sent! {}", notificationDTO);
		log.info("Notification Result {}", result);
	}

	@Override
	@JmsListener(destination = "ONESIGNAL_NOTIFICATIONBOX")
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
