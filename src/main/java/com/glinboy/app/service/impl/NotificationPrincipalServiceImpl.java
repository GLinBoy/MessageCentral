package com.glinboy.app.service.impl;

import com.glinboy.app.repository.NotificationPrincipalRepository;
import com.glinboy.app.service.NotificationChannelService;
import com.glinboy.app.service.dto.NotificationDTO;
import com.glinboy.app.service.mapper.NotificationDataMapper;
import com.glinboy.app.service.mapper.NotificationMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
@Transactional
public class NotificationPrincipalServiceImpl extends NotificationServiceImpl {

    public NotificationPrincipalServiceImpl(
        NotificationPrincipalRepository notificationRepository,
        NotificationMapper notificationMapper,
        NotificationChannelService<NotificationDTO> notificationProviderService,
        NotificationDataMapper notificationDataMapper
    ) {
        super(notificationRepository, notificationMapper, notificationProviderService, notificationDataMapper);
    }
}
