package com.glinboy.app.service.mapper;

import com.glinboy.app.domain.*;
import com.glinboy.app.service.dto.ShortMessageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ShortMessage} and its DTO {@link ShortMessageDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ShortMessageMapper extends EntityMapper<ShortMessageDTO, ShortMessage> {}
