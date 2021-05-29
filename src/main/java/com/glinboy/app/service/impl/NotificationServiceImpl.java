package com.glinboy.app.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glinboy.app.domain.Notification;
import com.glinboy.app.repository.NotificationRepository;
import com.glinboy.app.service.NotificationChannelService;
import com.glinboy.app.service.NotificationService;
import com.glinboy.app.service.dto.NotificationDTO;
import com.glinboy.app.service.dto.NotificationsDTO;
import com.glinboy.app.service.mapper.NotificationDataMapper;
import com.glinboy.app.service.mapper.NotificationMapper;

/**
 * Service Implementation for managing {@link Notification}.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    private final NotificationDataMapper notificationDataMapper;

    private final NotificationChannelService<NotificationDTO> notificationProviderService;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
            NotificationMapper notificationMapper,
            NotificationChannelService<NotificationDTO> notificationProviderService,
            NotificationDataMapper notificationDataMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.notificationProviderService = notificationProviderService;
        this.notificationDataMapper = notificationDataMapper;
    }

    @Override
    public NotificationDTO save(NotificationDTO notificationDTO) {
        log.debug("Request to save Notification : {}", notificationDTO);
        final Notification notification = notificationMapper.toEntity(notificationDTO);
        notification.getData().forEach(d -> d.setNotification(notification));
        NotificationDTO dto = notificationMapper.toDto(notificationRepository.save(notification));
        notificationProviderService.sendMessage(dto);
        return dto;
    }

    @Override
    public List<NotificationDTO> save(List<NotificationsDTO> notificationsDTO) {
        log.debug("Request to save Notification : {}", notificationsDTO);
        List<Notification> notifications = notificationsDTO.stream()
                .flatMap( ns -> ns.getReceivers().keySet()
                    .stream().filter(rk -> rk.length() <= 64 &&
                            ns.getReceivers().get(rk).length() <= 164)
                    .map(r -> {
                        Notification n = new Notification();
                        n.setUsername(r);
                        n.setToken(ns.getReceivers().get(r));
                        n.setSubject(ns.getSubject());
                        n.setContent(ns.getContent());
                        n.setImage(ns.getImage());
                        n.setData(ns.getData().stream().map(notificationDataMapper::toEntity)
                                .collect(Collectors.toSet()));
                        return n;
                        }))
                .collect(Collectors.toList());
            log.info("List of {} Emails: {}", notifications.size(), notifications);
            notifications = this.notificationRepository.saveAll(notifications);
            List<NotificationDTO> dtoList = this.notificationMapper.toDto(notifications);
            this.notificationProviderService.sendMessage(dtoList.toArray(new NotificationDTO[dtoList.size()]));
            return dtoList;
    }

    @Override
    public Optional<NotificationDTO> partialUpdate(NotificationDTO notificationDTO) {
        log.debug("Request to partially update Notification : {}", notificationDTO);

        return notificationRepository
            .findById(notificationDTO.getId())
            .map(
                existingNotification -> {
                    notificationMapper.partialUpdate(existingNotification, notificationDTO);
                    return existingNotification;
                }
            )
            .map(notificationRepository::save)
            .map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Notifications");
        return notificationRepository.findAll(pageable).map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationDTO> findOne(Long id) {
        log.debug("Request to get Notification : {}", id);
        return notificationRepository.findById(id).map(notificationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Notification : {}", id);
        notificationRepository.deleteById(id);
    }
}
