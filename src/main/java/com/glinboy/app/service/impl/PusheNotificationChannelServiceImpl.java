package com.glinboy.app.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.glinboy.app.repository.NotificationRepository;
import com.glinboy.app.service.NotificationChannelService;
import com.glinboy.app.service.dto.NotificationDTO;
import com.glinboy.app.service.mapper.NotificationMapper;

@Service
@ConditionalOnProperty(value = "application.notification.provider", havingValue = "pushe")
public class PusheNotificationChannelServiceImpl extends GenericChannelServiceImpl<NotificationDTO>
        implements NotificationChannelService<NotificationDTO> {

    public static final String TOPIC_NAME = "PUSHE_NOTIFICATIONBOX";

    public final NotificationRepository notificationRepository;
    
    private final NotificationMapper notificationMapper;

    protected PusheNotificationChannelServiceImpl(JmsTemplate jmsTemplate,
            ApplicationProperties properties,
            NotificationRepository notificationRepository,
            NotificationMapper notificationMapper) {
        super(jmsTemplate, properties);
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    String getTopicName() {
        return TOPIC_NAME;
    }

    @Override
    public Consumer<NotificationDTO[]> saveFunction() {
        return emails -> notificationRepository.saveAll(Stream
                .of(emails)
                .map(notificationMapper::toEntity)
                .collect(Collectors.toList()));
    }

    @Override
    public void deliverMessage(NotificationDTO... notificationDTOs) {
        for (var i = 0; i < notificationDTOs.length; i++) {
            NotificationDTO notificationDTO = notificationDTOs[i];
            var restTemplate = new RestTemplate();
            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
            headers.set("authorization", "Token ".concat(properties.getNotification().getPushe().getToken()));
            var requestBody = new JSONObject();
            requestBody.put("app_ids", properties.getNotification().getPushe().getAppId());
            var filters = new JSONObject();
            filters.put("device_id", new JSONArray(List.of(notificationDTO.getToken())));
            requestBody.put("filters", filters);
            var data = new JSONObject();
            data.put("title", notificationDTO.getSubject());
            data.put("content", notificationDTO.getContent());
            requestBody.put("data", data);
            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
            String result = restTemplate.postForObject(properties.getNotification().getPushe().getUrl(), request,
                    String.class);
            log.info("Notification sent! {}", notificationDTO);
            log.info("Notification Result {}", result);
            updateStatusToSent(notificationDTO);
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
