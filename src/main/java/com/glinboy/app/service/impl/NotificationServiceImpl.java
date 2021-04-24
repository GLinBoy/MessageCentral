package com.glinboy.app.service.impl;

import com.glinboy.app.domain.Notification;
import com.glinboy.app.repository.NotificationRepository;
import com.glinboy.app.service.NotificationProviderService;
import com.glinboy.app.service.NotificationService;
import com.glinboy.app.service.dto.NotificationDTO;
import com.glinboy.app.service.mapper.NotificationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Notification}.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    private final NotificationProviderService<NotificationDTO> notificationProviderService;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
    		NotificationMapper notificationMapper,
    		NotificationProviderService<NotificationDTO> notificationProviderService) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.notificationProviderService = notificationProviderService;
    }

    @Override
    public NotificationDTO save(NotificationDTO notificationDTO) {
        log.debug("Request to save Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        NotificationDTO dto = notificationMapper.toDto(notification);
        notificationProviderService.sendNotification(dto);
        return dto;
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
