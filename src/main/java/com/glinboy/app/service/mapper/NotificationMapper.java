package com.glinboy.app.service.mapper;

import com.glinboy.app.domain.Notification;
import com.glinboy.app.service.dto.NotificationDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring", uses = {NotificationDataMapper.class})
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NotificationDTO toDtoId(Notification notification);
}
