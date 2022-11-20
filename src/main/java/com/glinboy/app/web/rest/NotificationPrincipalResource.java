package com.glinboy.app.web.rest;

import com.glinboy.app.repository.NotificationRepository;
import com.glinboy.app.service.NotificationQueryService;
import com.glinboy.app.service.NotificationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class NotificationPrincipalResource extends NotificationResource {

    public NotificationPrincipalResource(
        NotificationService notificationService,
        NotificationRepository notificationRepository,
        NotificationQueryService notificationQueryService
    ) {
        super(notificationService, notificationRepository, notificationQueryService);
    }
}
