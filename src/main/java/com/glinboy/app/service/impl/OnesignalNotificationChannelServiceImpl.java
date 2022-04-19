package com.glinboy.app.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.service.dto.NotificationDTO;
import com.glinboy.app.service.dto.NotificationDataDTO;

@Service
@ConditionalOnProperty(value = "application.notification.provider", havingValue = "onesignal", matchIfMissing = true)
public class OnesignalNotificationChannelServiceImpl extends AbstractNotificationChannelServiceImpl {

    protected OnesignalNotificationChannelServiceImpl(ApplicationEventPublisher publisher,
            ApplicationProperties properties) {
        super(publisher, properties);
    }

    @Override
    public void deliverMessage(NotificationDTO... notificationDTOs) {
        for (var i = 0; i < notificationDTOs.length; i++) {
            NotificationDTO notificationDTO = notificationDTOs[i];
            var restTemplate = new RestTemplate();
            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
            var requestBody = new JSONObject();
            requestBody.put("app_id", properties.getNotification().getOnesignal().getAppId());
            requestBody.put("include_player_ids", new JSONArray(List.of(notificationDTO.getToken())));
            var headings = new JSONObject();
            headings.put("en", notificationDTO.getSubject());
            requestBody.put("headings", headings);
            var contents = new JSONObject();
            contents.put("en", notificationDTO.getContent());
            requestBody.put("contents", contents);
            if (notificationDTO.getData() != null && !notificationDTO.getData().isEmpty()) {
                requestBody.put("data", notificationDTO.getData().stream()
                        .collect(Collectors.toMap(NotificationDataDTO::getKey, NotificationDataDTO::getValue)));
            }
            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
            String result = restTemplate.postForObject(properties.getNotification().getOnesignal().getUrl(), request,
                    String.class);
            log.info("Notification sent! {}", notificationDTO);
            log.info("Notification Result {}", result);
        }
    }

}
