package com.glinboy.app.service.impl;

import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.service.dto.NotificationDTO;
import com.glinboy.app.service.dto.NotificationDataDTO;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message.Builder;
import com.google.firebase.messaging.Notification;

@Service
@ConditionalOnProperty(value = "application.notification.provider", havingValue = "firebase")
public class FirebaseNotificationChannelServiceImpl extends AbstractNotificationChannelServiceImpl {

    private final FirebaseMessaging firebaseMessaging;

    protected FirebaseNotificationChannelServiceImpl(ApplicationEventPublisher publisher,
            ApplicationProperties properties,
            FirebaseMessaging firebaseMessaging) {
        super(publisher, properties);
        this.firebaseMessaging = firebaseMessaging;
    }

    @Override
    public void deliverMessage(NotificationDTO... notificationDTOs) {
        for (var i = 0; i < notificationDTOs.length; i++) {
            try {
                NotificationDTO notificationDTO = notificationDTOs[i];
                Notification notification = Notification.builder()
                        .setTitle(notificationDTO.getSubject())
                        .setBody(notificationDTO.getContent())
                        .build();

                Builder message = com.google.firebase.messaging.Message.builder()
                        .setToken(notificationDTO.getToken())
                        .setNotification(notification);
                if (notificationDTO.getData() != null && !notificationDTO.getData().isEmpty()) {
                    message.putAllData(notificationDTO.getData().stream()
                            .collect(Collectors.toMap(NotificationDataDTO::getKey, NotificationDataDTO::getValue)));
                }

                String result = firebaseMessaging.send(message.build());
                log.info("Notification sent! {}", notificationDTO);
                log.info("Notification Result {}", result);
            } catch (FirebaseMessagingException ex) {
                log.error("Sending message failed: {}", ex.getMessage(), ex);
            }
        }
    }

}
