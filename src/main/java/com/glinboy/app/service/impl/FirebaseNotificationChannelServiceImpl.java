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
import com.glinboy.app.domain.enumeration.MessageStatus;
import com.glinboy.app.service.NotificationChannelService;
import com.glinboy.app.service.NotificationService;
import com.glinboy.app.service.dto.NotificationDTO;
import com.glinboy.app.service.dto.NotificationDataDTO;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message.Builder;
import com.google.firebase.messaging.Notification;

@Service
@ConditionalOnProperty(value = "application.notification.provider", havingValue = "firebase")
public class FirebaseNotificationChannelServiceImpl extends GenericChannelServiceImpl<NotificationDTO>
        implements NotificationChannelService<NotificationDTO> {

    public static final String TOPIC_NAME = "FIREBASE_NOTIFICATIONBOX";

    private final FirebaseMessaging firebaseMessaging;

    private final NotificationService notificationService;

    protected FirebaseNotificationChannelServiceImpl(JmsTemplate jmsTemplate,
            ApplicationProperties properties,
            FirebaseMessaging firebaseMessaging,
            NotificationService notificationService) {
        super(jmsTemplate, properties);
        this.firebaseMessaging = firebaseMessaging;
        this.notificationService = notificationService;
    }

    @Override
    String getTopicName() {
        return TOPIC_NAME;
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
                if (!CollectionUtils.isEmpty(notificationDTO.getData())) {
                    message.putAllData(notificationDTO.getData().stream()
                            .collect(Collectors.toMap(NotificationDataDTO::getKey, NotificationDataDTO::getValue)));
                }

                String result = firebaseMessaging.send(message.build());
                notificationDTO.setStatus(MessageStatus.SENT);
                log.info("Notification sent! {}", notificationDTO);
                log.info("Notification Result {}", result);
                notificationService.save(notificationDTO);
            } catch (FirebaseMessagingException ex) {
                log.error("Sending message failed: {}", ex.getMessage(), ex);
            }
        }
    }

    @Override
    @JmsListener(destination = FirebaseNotificationChannelServiceImpl.TOPIC_NAME)
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
