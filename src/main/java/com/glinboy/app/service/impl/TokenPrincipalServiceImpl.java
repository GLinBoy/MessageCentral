package com.glinboy.app.service.impl;

import com.glinboy.app.domain.Token;
import com.glinboy.app.repository.TokenRepository;
import com.glinboy.app.security.AuthoritiesConstants;
import com.glinboy.app.security.jwt.TokenProvider;
import com.glinboy.app.service.dto.TokenDTO;
import com.glinboy.app.service.mapper.TokenMapper;
import java.time.Instant;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Primary
@Transactional
public class TokenPrincipalServiceImpl extends TokenServiceImpl {

    private final Logger log = LoggerFactory.getLogger(TokenPrincipalServiceImpl.class);

    private final TokenRepository tokenRepository;

    private final TokenMapper tokenMapper;

    private final TokenProvider tokenProvider;

    public TokenPrincipalServiceImpl(TokenRepository tokenRepository, TokenMapper tokenMapper, TokenProvider tokenProvider) {
        super(tokenRepository, tokenMapper, tokenProvider);
        this.tokenRepository = tokenRepository;
        this.tokenMapper = tokenMapper;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public TokenDTO save(TokenDTO tokenDTO) {
        log.debug("Request to save Token : {}", tokenDTO);
        Token token = tokenMapper.toEntity(tokenDTO);
        if (StringUtils.isBlank(token.getToken())) {
            token.setToken(generateToken(tokenDTO));
        }
        token = tokenRepository.save(token);
        return tokenMapper.toDto(token);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Token : {}", id);
        Token token =
            this.tokenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find token with id: " + id));
        if (Instant.now().isBefore(token.getDeprecateAt())) {
            this.tokenRepository.updateTokenStatus(id, Boolean.TRUE);
        } else {
            tokenRepository.deleteById(id);
        }
    }

    @Override
    public void enableToken(Long id) {
        this.tokenRepository.updateTokenStatus(id, Boolean.FALSE);
    }

    @Override
    public void disableToken(Long id) {
        this.tokenRepository.updateTokenStatus(id, Boolean.TRUE);
    }

    public String generateToken(TokenDTO tokenDTO) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            tokenDTO.getName(),
            null,
            getTokenRoles(tokenDTO.getRoles())
        );
        return tokenProvider.createToken(authentication, false);
    }

    /**
     * Roles are between 1-7 and its in binary format. First position from right, present email
     * role, the next one present SMS role and the last one present Notification role the result of
     * theme has been save as a integer number. Check the blow table (Use as a reference): <br/>
     * +---+---+---+---+<br/> | N | S | E |   |<br/> +===+===+===+===+<br/> | 0 | 0 | 1 | 1 |<br/>
     * +---+---+---+---+<br/> | 0 | 1 | 0 | 2 |<br/> +---+---+---+---+<br/> | 0 | 1 | 1 | 3 |<br/>
     * +---+---+---+---+<br/> | 1 | 0 | 0 | 4 |<br/> +---+---+---+---+<br/> | 1 | 0 | 1 | 5 |<br/>
     * +---+---+---+---+<br/> | 1 | 1 | 0 | 6 |<br/> +---+---+---+---+<br/> | 1 | 1 | 1 | 7 |<br/>
     * +---+---+---+---+<br/>
     *
     * @param tokenRoles TokenDTO/Token roles filed
     * @return a Set of authority that token present.
     */
    private Set<GrantedAuthority> getTokenRoles(Integer tokenRoles) {
        switch (tokenRoles) {
            case 1:
                return Set.of(new SimpleGrantedAuthority(AuthoritiesConstants.EMAIL_USER));
            case 2:
                return Set.of(new SimpleGrantedAuthority(AuthoritiesConstants.SMS_USER));
            case 3:
                return Set.of(
                    new SimpleGrantedAuthority(AuthoritiesConstants.EMAIL_USER),
                    new SimpleGrantedAuthority(AuthoritiesConstants.SMS_USER)
                );
            case 4:
                return Set.of(new SimpleGrantedAuthority(AuthoritiesConstants.NOTIFICATION_USER));
            case 5:
                return Set.of(
                    new SimpleGrantedAuthority(AuthoritiesConstants.EMAIL_USER),
                    new SimpleGrantedAuthority(AuthoritiesConstants.NOTIFICATION_USER)
                );
            case 6:
                return Set.of(
                    new SimpleGrantedAuthority(AuthoritiesConstants.SMS_USER),
                    new SimpleGrantedAuthority(AuthoritiesConstants.NOTIFICATION_USER)
                );
            case 7:
                return Set.of(
                    new SimpleGrantedAuthority(AuthoritiesConstants.EMAIL_USER),
                    new SimpleGrantedAuthority(AuthoritiesConstants.SMS_USER),
                    new SimpleGrantedAuthority(AuthoritiesConstants.NOTIFICATION_USER)
                );
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Roles is unclear!");
        }
    }
}
