package com.glinboy.app.service.impl;

import com.glinboy.app.domain.Token;
import com.glinboy.app.repository.TokenRepository;
import com.glinboy.app.service.TokenService;
import com.glinboy.app.service.dto.TokenDTO;
import com.glinboy.app.service.mapper.TokenMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Token}.
 */
@Service
@Transactional
public class TokenServiceImpl implements TokenService {

    private final Logger log = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final TokenRepository tokenRepository;

    private final TokenMapper tokenMapper;

    public TokenServiceImpl(TokenRepository tokenRepository, TokenMapper tokenMapper) {
        this.tokenRepository = tokenRepository;
        this.tokenMapper = tokenMapper;
    }

    @Override
    public TokenDTO save(TokenDTO tokenDTO) {
        log.debug("Request to save Token : {}", tokenDTO);
        Token token = tokenMapper.toEntity(tokenDTO);
        token = tokenRepository.save(token);
        return tokenMapper.toDto(token);
    }

    @Override
    public TokenDTO update(TokenDTO tokenDTO) {
        log.debug("Request to save Token : {}", tokenDTO);
        Token token = tokenMapper.toEntity(tokenDTO);
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
}
