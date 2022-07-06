package com.glinboy.app.service.mapper;

import com.glinboy.app.domain.ShortMessage;
import com.glinboy.app.service.dto.ShortMessageDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link ShortMessage} and its DTO {@link ShortMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShortMessageMapper extends EntityMapper<ShortMessageDTO, ShortMessage> {
}
