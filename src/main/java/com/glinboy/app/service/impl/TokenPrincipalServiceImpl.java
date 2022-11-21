package com.glinboy.app.service.impl;

import com.glinboy.app.repository.TokenRepository;
import com.glinboy.app.security.jwt.TokenProvider;
import com.glinboy.app.service.mapper.TokenMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
@Transactional
public class TokenPrincipalServiceImpl extends TokenServiceImpl {

    public TokenPrincipalServiceImpl(TokenRepository tokenRepository, TokenMapper tokenMapper, TokenProvider tokenProvider) {
        super(tokenRepository, tokenMapper, tokenProvider);
    }
}
