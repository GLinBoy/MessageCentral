package com.glinboy.app.service.impl;

import com.glinboy.app.domain.Token;
import com.glinboy.app.repository.TokenRepository;
import com.glinboy.app.security.AuthoritiesConstants;
import com.glinboy.app.security.jwt.TokenProvider;
import com.glinboy.app.service.TokenService;
import com.glinboy.app.service.dto.TokenDTO;
import com.glinboy.app.service.mapper.TokenMapper;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service Implementation for managing {@link Token}.
 */
@Service
@Transactional
public class TokenServiceImpl implements TokenService {

    private final Logger log = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final TokenRepository tokenRepository;

    private final TokenMapper tokenMapper;

    private final TokenProvider tokenProvider;

    public TokenServiceImpl(TokenRepository tokenRepository, TokenMapper tokenMapper, TokenProvider tokenProvider) {
        this.tokenRepository = tokenRepository;
        this.tokenMapper = tokenMapper;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public TokenDTO save(TokenDTO tokenDTO) {
        log.debug("Request to save Token : {}", tokenDTO);
        Token token = tokenMapper.toEntity(tokenDTO);
        if (token.getCreatedAt() == null) {
            token.createdAt(Instant.now());
        }
        if (StringUtils.isBlank(token.getToken())) {
            token.setToken(generateToken(tokenDTO));
        }
        token = tokenRepository.save(token);
        return tokenMapper.toDto(token);
    }

    @Override
    public Optional<TokenDTO> partialUpdate(TokenDTO tokenDTO) {
        log.debug("Request to partially update Token : {}", tokenDTO);

        return tokenRepository
            .findById(tokenDTO.getId())
            .map(existingToken -> {
                tokenMapper.partialUpdate(existingToken, tokenDTO);

                return existingToken;
            })
            .map(tokenRepository::save)
            .map(tokenMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TokenDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Tokens");
        return tokenRepository.findAll(pageable).map(tokenMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TokenDTO> findOne(Long id) {
        log.debug("Request to get Token : {}", id);
        return tokenRepository.findById(id).map(tokenMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Token : {}", id);
        tokenRepository.deleteById(id);
    }

    public String generateToken(TokenDTO tokenDTO) {
        return "Test Token";
    }
}
