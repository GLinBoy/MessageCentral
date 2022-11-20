package com.glinboy.app.web.rest;

import com.glinboy.app.repository.TokenRepository;
import com.glinboy.app.service.TokenQueryService;
import com.glinboy.app.service.TokenService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TokenPrincipalResource extends TokenResource {

    public TokenPrincipalResource(TokenService tokenService, TokenRepository tokenRepository, TokenQueryService tokenQueryService) {
        super(tokenService, tokenRepository, tokenQueryService);
    }
}
