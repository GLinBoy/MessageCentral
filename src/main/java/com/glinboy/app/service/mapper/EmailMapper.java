package com.glinboy.app.service.mapper;

import com.glinboy.app.domain.Email;
import com.glinboy.app.service.dto.EmailDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Email} and its DTO {@link EmailDTO}.
 */
@Mapper(componentModel = "spring")
public interface EmailMapper extends EntityMapper<EmailDTO, Email> {}
