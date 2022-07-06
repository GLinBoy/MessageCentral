package com.glinboy.app.service.mapper;

import com.glinboy.app.domain.Notification;
import com.glinboy.app.domain.NotificationData;
import com.glinboy.app.service.dto.NotificationDTO;
import com.glinboy.app.service.dto.NotificationDataDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface NotificationDataMapper extends EntityMapper<NotificationDataDTO, NotificationData> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NotificationDataDTO toDtoId(NotificationData notificationData);

    @Mapping(source = "notification.id", target = "notificationId")
    NotificationDataDTO toDto(NotificationData notificationData);
}
