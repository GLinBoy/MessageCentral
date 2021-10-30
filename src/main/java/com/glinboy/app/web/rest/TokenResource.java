package com.glinboy.app.web.rest;

import com.glinboy.app.domain.Token;
import com.glinboy.app.repository.TokenRepository;
import com.glinboy.app.rsql.CustomRsqlVisitor;
import com.glinboy.app.service.TokenQueryService;
import com.glinboy.app.service.TokenService;
import com.glinboy.app.service.dto.TokenDTO;
import com.glinboy.app.web.rest.errors.BadRequestAlertException;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.glinboy.app.domain.Token}.
 */
@RestController
@RequestMapping("/api")
public class TokenResource {

    private final Logger log = LoggerFactory.getLogger(TokenResource.class);

    private static final String ENTITY_NAME = "token";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TokenService tokenService;

    private final TokenRepository tokenRepository;

    private final TokenQueryService tokenQueryService;

    public TokenResource(TokenService tokenService, TokenRepository tokenRepository, TokenQueryService tokenQueryService) {
        this.tokenService = tokenService;
        this.tokenRepository = tokenRepository;
        this.tokenQueryService = tokenQueryService;
    }

    /**
     * {@code POST  /tokens} : Create a new token.
     *
     * @param tokenDTO the tokenDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new tokenDTO, or with status {@code 400 (Bad Request)} if
     *         the token has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tokens")
    public ResponseEntity<TokenDTO> createToken(@Valid @RequestBody TokenDTO tokenDTO) throws URISyntaxException {
        log.debug("REST request to save Token : {}", tokenDTO);
        if (tokenDTO.getId() != null) {
            throw new BadRequestAlertException("A new token cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TokenDTO result = tokenService.save(tokenDTO);
        return ResponseEntity
            .created(new URI("/api/tokens/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tokens/:id} : Updates an existing token.
     *
     * @param id       the id of the tokenDTO to save.
     * @param tokenDTO the tokenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated tokenDTO, or with status {@code 400 (Bad Request)} if the
     *         tokenDTO is not valid, or with status
     *         {@code 500 (Internal Server Error)} if the tokenDTO couldn't be
     *         updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tokens/{id}")
    public ResponseEntity<TokenDTO> updateToken(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TokenDTO tokenDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Token : {}, {}", id, tokenDTO);
        if (tokenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tokenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tokenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TokenDTO result = tokenService.save(tokenDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tokenDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tokens/:id} : Partial updates given fields of an existing
     * token, field will ignore if it is null
     *
     * @param id       the id of the tokenDTO to save.
     * @param tokenDTO the tokenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated tokenDTO, or with status {@code 400 (Bad Request)} if the
     *         tokenDTO is not valid, or with status {@code 404 (Not Found)} if the
     *         tokenDTO is not found, or with status
     *         {@code 500 (Internal Server Error)} if the tokenDTO couldn't be
     *         updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tokens/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TokenDTO> partialUpdateToken(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TokenDTO tokenDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Token partially : {}, {}", id, tokenDTO);
        if (tokenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tokenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tokenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TokenDTO> result = tokenService.partialUpdate(tokenDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tokenDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /tokens} : get all the tokens.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of tokens in body.
     */
    @GetMapping("/tokens")
    public ResponseEntity<List<TokenDTO>> getAllTokens(
        Pageable pageable,
        @RequestParam(value = "query", required = false, defaultValue = "") String query
    ) {
        Specification<Token> specs = Specification.where(null);
        if (!StringUtils.isBlank(query)) {
            Node rootNode = new RSQLParser().parse(query);
            specs = rootNode.accept(new CustomRsqlVisitor<Token>());
        }
        Page<TokenDTO> page = tokenQueryService.findBySearch(specs, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tokens/count} : count all the tokens.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/tokens/count")
    public ResponseEntity<Long> countTokens(TokenCriteria criteria) {
        log.debug("REST request to count Tokens by criteria: {}", criteria);
        return ResponseEntity.ok().body(tokenQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /tokens/:id} : get the "id" token.
     *
     * @param id the id of the tokenDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tokenDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tokens/{id}")
    public ResponseEntity<TokenDTO> getToken(@PathVariable Long id) {
        log.debug("REST request to get Token : {}", id);
        Optional<TokenDTO> tokenDTO = tokenService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tokenDTO);
    }

    /**
     * {@code DELETE  /tokens/:id} : delete the "id" token.
     *
     * @param id the id of the tokenDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tokens/{id}")
    public ResponseEntity<Void> deleteToken(@PathVariable Long id) {
        log.debug("REST request to delete Token : {}", id);
        tokenService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
