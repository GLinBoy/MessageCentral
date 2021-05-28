package com.glinboy.app.service.impl;

import java.util.stream.Collectors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.service.NotificationProviderService;
import com.glinboy.app.service.dto.NotificationDTO;
import com.glinboy.app.service.dto.NotificationDataDTO;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message.Builder;
import com.google.firebase.messaging.Notification;

@Service
@ConditionalOnProperty(value = "application.notification.provider", havingValue = "firebase")
public class FirebaseNotificationProviderServiceImpl extends GenericChannelServiceImpl<NotificationDTO>
		implements NotificationProviderService<NotificationDTO> {

	public static final String TOPIC_NAME = "FIREBASE_NOTIFICATIONBOX";

	private final FirebaseMessaging firebaseMessaging;

	protected FirebaseNotificationProviderServiceImpl(JmsTemplate jmsTemplate,
			ApplicationProperties properties,
			FirebaseMessaging firebaseMessaging) {
		super(jmsTemplate, properties);
		this.firebaseMessaging = firebaseMessaging;
	}

	@Override
	String getTopicName() {
		return TOPIC_NAME;
	}

	@Override
	public void deliverMessage(NotificationDTO... notificationDTOs) {
		for (var i = 0; i < notificationDTOs.length; i++) {
			try {
				Notification notification = Notification.builder()
						.setTitle(notificationDTOs[i].getSubject())
						.setBody(notificationDTOs[i].getContent())
						.build();
	
				Builder message = com.google.firebase.messaging.Message.builder()
						.setToken(notificationDTOs[i].getToken())
						.setNotification(notification);
				if(!CollectionUtils.isEmpty(notificationDTOs[i].getData())) {
					message.putAllData(notificationDTOs[i].getData().stream()
							.collect(Collectors.toMap(NotificationDataDTO::getKey,
									NotificationDataDTO::getValue)));
				}
	
				String result = firebaseMessaging.send(message.build());
				log.info("Notification sent! {}", notificationDTOs[i]);
				log.info("Notification Result {}", result);
			} catch (FirebaseMessagingException ex) {
				log.error("Sending message failed: {}", ex.getMessage(), ex);
			}
		}
	}

	@Override
	@JmsListener(destination = FirebaseNotificationProviderServiceImpl.TOPIC_NAME)
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
