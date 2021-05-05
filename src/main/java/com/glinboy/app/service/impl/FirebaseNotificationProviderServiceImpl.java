package com.glinboy.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.glinboy.app.service.NotificationProviderService;
import com.glinboy.app.service.dto.NotificationDTO;
import com.glinboy.app.service.dto.NotificationDataDTO;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message.Builder;
import com.google.firebase.messaging.Notification;

@Service
@ConditionalOnProperty(value = "application.notification.provider", havingValue = "firebase")
public class FirebaseNotificationProviderServiceImpl
		implements NotificationProviderService<NotificationDTO>, MessageListener {

	private final Logger log = LoggerFactory.getLogger(FirebaseNotificationProviderServiceImpl.class);

	private final JmsTemplate jmsTemplate;

	private final FirebaseMessaging firebaseMessaging;

	public FirebaseNotificationProviderServiceImpl(JmsTemplate jmsTemplate,
			FirebaseMessaging firebaseMessaging) {
		this.jmsTemplate = jmsTemplate;
		this.firebaseMessaging = firebaseMessaging;
	}

	@Override
	public void sendNotification(NotificationDTO notificationDTO) {
		jmsTemplate.convertAndSend("FIREBASE_NOTIFICATIONBOX", notificationDTO);
	}

	@Override
	public void sendNotification(List<NotificationDTO> notificationDTOs) {
		notificationDTOs.forEach(n -> jmsTemplate.convertAndSend("FIREBASE_NOTIFICATIONBOX", n));
	}

	private void deliverNotification(NotificationDTO notificationDTO) {
		try {
			Notification notification = Notification.builder()
					.setTitle(notificationDTO.getSubject())
					.setBody(notificationDTO.getContent())
					.build();

			Builder message = com.google.firebase.messaging.Message.builder()
					.setToken(notificationDTO.getToken())
					.setNotification(notification);
			if(!CollectionUtils.isEmpty(notificationDTO.getData())) {
				message.putAllData(notificationDTO.getData().stream()
						.collect(Collectors.toMap(NotificationDataDTO::getKey,
								NotificationDataDTO::getValue)));
			}

			String result = firebaseMessaging.send(message.build());
			log.info("Notification sent! {}", notificationDTO);
			log.info("Notification Result {}", result);
		} catch (FirebaseMessagingException ex) {
			log.error("Sending message failed: {}", ex.getMessage(), ex);
		}
	}

	@Override
	@JmsListener(destination = "FIREBASE_NOTIFICATIONBOX")
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
