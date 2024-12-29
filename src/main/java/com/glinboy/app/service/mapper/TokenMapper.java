package com.glinboy.app.service.mapper;

import com.glinboy.app.domain.Token;
import com.glinboy.app.service.dto.TokenDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Token} and its DTO {@link TokenDTO}.
 */
@Mapper(componentModel = "spring")
public interface TokenMapper extends EntityMapper<TokenDTO, Token> {}
