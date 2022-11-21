package com.glinboy.app.service.impl;

import com.glinboy.app.domain.Notification;
import com.glinboy.app.domain.NotificationData;
import com.glinboy.app.domain.enumeration.MessageStatus;
import com.glinboy.app.event.NotificationSentFailedEvent;
import com.glinboy.app.event.NotificationSentSuccessfulEvent;
import com.glinboy.app.repository.NotificationPrincipalRepository;
import com.glinboy.app.repository.NotificationRepository;
import com.glinboy.app.service.NotificationChannelService;
import com.glinboy.app.service.dto.NotificationDTO;
import com.glinboy.app.service.dto.NotificationsDTO;
import com.glinboy.app.service.mapper.NotificationDataMapper;
import com.glinboy.app.service.mapper.NotificationMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
@Transactional
public class NotificationPrincipalServiceImpl extends NotificationServiceImpl {

    private final Logger log = LoggerFactory.getLogger(NotificationPrincipalServiceImpl.class);

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    private final NotificationDataMapper notificationDataMapper;

    private final NotificationChannelService<NotificationDTO> notificationProviderService;

    public NotificationPrincipalServiceImpl(
        NotificationPrincipalRepository notificationRepository,
        NotificationMapper notificationMapper,
        NotificationChannelService<NotificationDTO> notificationProviderService,
        NotificationDataMapper notificationDataMapper
    ) {
        super(notificationRepository, notificationMapper, notificationProviderService, notificationDataMapper);
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.notificationDataMapper = notificationDataMapper;
        this.notificationProviderService = notificationProviderService;
    }

    @Override
    public NotificationDTO save(NotificationDTO notificationDTO) {
        log.debug("Request to save Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        if (notification.getId() == null) {
            notification.setStatus(MessageStatus.IN_QUEUE);
        }
        notification.getData().forEach(d -> d.setNotification(notification));
        NotificationDTO dto = notificationMapper.toDto(notificationRepository.save(notification));
        notificationProviderService.sendMessage(dto);
        return dto;
    }

    @Override
    public List<NotificationDTO> save(List<NotificationsDTO> notificationsDTO) {
        log.debug("Request to save Notification : {}", notificationsDTO);
        List<Notification> notifications = notificationsDTO
            .stream()
            .flatMap(ns ->
                ns
                    .getReceivers()
                    .stream()
                    .map(r -> {
                        Notification n = new Notification();
                        n.setUsername(r.getUsername());
                        n.setToken(r.getToken());
                        n.setSubject(ns.getSubject());
                        n.setContent(ns.getContent());
                        n.setImage(ns.getImage());
                        n.setStatus(MessageStatus.IN_QUEUE);
                        n.setData(
                            ns
                                .getData()
                                .stream()
                                .map(ndto -> {
                                    NotificationData nd = notificationDataMapper.toEntity(ndto);
                                    nd.setNotification(n);
                                    return nd;
                                })
                                .collect(Collectors.toSet())
                        );
                        return n;
                    })
            )
            .collect(Collectors.toList());
        log.info("List of {} Notification: {}", notifications.size(), notifications);
        notifications = this.notificationRepository.saveAll(notifications);
        List<NotificationDTO> dtoList = this.notificationMapper.toDto(notifications);
        this.notificationProviderService.sendMessage(dtoList.toArray(new NotificationDTO[dtoList.size()]));
        return dtoList;
    }

    @Transactional
    @EventListener
    public void onMessageSent(NotificationSentSuccessfulEvent event) {
        this.notificationRepository.updateStatus(MessageStatus.SENT, event.getIds().toArray(new Long[event.getIds().size()]));
    }

    @Transactional
    @EventListener
    public void onMessageFailed(NotificationSentFailedEvent event) {
        this.notificationRepository.updateStatus(MessageStatus.FAILED, event.getIds().toArray(new Long[event.getIds().size()]));
    }
}
