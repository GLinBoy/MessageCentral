package com.glinboy.app.service.impl;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.service.dto.NotificationDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@ConditionalOnProperty(value = "application.notification.provider", havingValue = "pushe")
public class PusheNotificationChannelServiceImpl extends AbstractNotificationChannelServiceImpl {

    protected PusheNotificationChannelServiceImpl(ApplicationEventPublisher publisher,
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
        }
    }

}
