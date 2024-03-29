package com.glinboy.app.web.rest;

import com.glinboy.app.domain.ShortMessage;
import com.glinboy.app.repository.ShortMessageRepository;
import com.glinboy.app.rsql.CustomRsqlVisitor;
import com.glinboy.app.service.ShortMessageQueryService;
import com.glinboy.app.service.ShortMessageService;
import com.glinboy.app.service.dto.ShortMessageDTO;
import com.glinboy.app.service.dto.ShortMessagesDTO;
import com.glinboy.app.web.rest.errors.BadRequestAlertException;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
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

@RestController
@RequestMapping("/api")
public class ShortMessagePrincipalResource extends ShortMessageResource {

    private static final String ENTITY_NAME = "shortMessage";
    private final Logger log = LoggerFactory.getLogger(ShortMessageResource.class);
    private final ShortMessageService shortMessageService;
    private final ShortMessageRepository shortMessageRepository;
    private final ShortMessageQueryService shortMessageQueryService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public ShortMessagePrincipalResource(
        ShortMessageService shortMessageService,
        ShortMessageRepository shortMessageRepository,
        ShortMessageQueryService shortMessageQueryService
    ) {
        this.shortMessageService = shortMessageService;
        this.shortMessageRepository = shortMessageRepository;
        this.shortMessageQueryService = shortMessageQueryService;
    }

    /**
     * {@code POST  /short-messages} : Create a new shortMessage.
     *
     * @param shortMessageDTO the shortMessageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shortMessageDTO, or with status {@code 400 (Bad Request)} if the shortMessage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/short-messages")
    public ResponseEntity<ShortMessageDTO> createShortMessage(@Valid @RequestBody ShortMessageDTO shortMessageDTO)
        throws URISyntaxException {
        log.debug("REST request to save ShortMessage : {}", shortMessageDTO);
        if (shortMessageDTO.getId() != null) {
            throw new BadRequestAlertException("A new shortMessage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ShortMessageDTO result = shortMessageService.save(shortMessageDTO);
        return ResponseEntity
            .created(new URI("/api/short-messages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST  /short-messages} : Create a new shortMessage.
     *
     * @param shortMessageDTO the shortMessageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shortMessageDTO, or with status {@code 400 (Bad Request)} if the shortMessage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/short-messages/multiple")
    public ResponseEntity<Void> createMultipleShortMessage(@Valid @RequestBody List<ShortMessagesDTO> shortMessagesDTO)
        throws URISyntaxException {
        log.debug("REST request to save ShortMessage : {}", shortMessagesDTO);
        shortMessageService.save(shortMessagesDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * {@code PUT  /short-messages/:id} : Updates an existing shortMessage.
     *
     * @param id              the id of the shortMessageDTO to save.
     * @param shortMessageDTO the shortMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shortMessageDTO,
     * or with status {@code 400 (Bad Request)} if the shortMessageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shortMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    //    @PutMapping("/short-messages/{id}")
    public ResponseEntity<ShortMessageDTO> updateShortMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ShortMessageDTO shortMessageDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ShortMessage : {}, {}", id, shortMessageDTO);
        if (shortMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shortMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shortMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ShortMessageDTO result = shortMessageService.update(shortMessageDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shortMessageDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /short-messages/:id} : Partial updates given fields of an existing shortMessage, field will ignore if it is null
     *
     * @param id              the id of the shortMessageDTO to save.
     * @param shortMessageDTO the shortMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shortMessageDTO,
     * or with status {@code 400 (Bad Request)} if the shortMessageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shortMessageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shortMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    //    @PatchMapping(value = "/short-messages/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<ShortMessageDTO> partialUpdateShortMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ShortMessageDTO shortMessageDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ShortMessage partially : {}, {}", id, shortMessageDTO);
        if (shortMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shortMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shortMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShortMessageDTO> result = shortMessageService.partialUpdate(shortMessageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shortMessageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /short-messages} : get all the shortMessages.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shortMessages in body.
     */
    @GetMapping("/short-messages")
    public ResponseEntity<List<ShortMessageDTO>> getAllShortMessages(
        @ParameterObject Pageable pageable,
        @RequestParam(value = "query", required = false, defaultValue = "") String query
    ) {
        Specification<ShortMessage> specs = Specification.where(null);
        if (!StringUtils.isBlank(query)) {
            Node rootNode = new RSQLParser().parse(query);
            specs = rootNode.accept(new CustomRsqlVisitor<ShortMessage>());
        }
        Page<ShortMessageDTO> page = shortMessageQueryService.findBySearch(specs, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /short-messages/count} : count all the shortMessages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/short-messages/count")
    public ResponseEntity<Long> countShortMessages(@RequestParam(value = "query", required = false, defaultValue = "") String query) {
        Specification<ShortMessage> specs = Specification.where(null);
        if (!StringUtils.isBlank(query)) {
            Node rootNode = new RSQLParser().parse(query);
            specs = rootNode.accept(new CustomRsqlVisitor<ShortMessage>());
        }
        return ResponseEntity.ok().body(shortMessageQueryService.countBySpecification(specs));
    }

    /**
     * {@code GET  /short-messages/:id} : get the "id" shortMessage.
     *
     * @param id the id of the shortMessageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shortMessageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/short-messages/{id}")
    public ResponseEntity<ShortMessageDTO> getShortMessage(@PathVariable Long id) {
        log.debug("REST request to get ShortMessage : {}", id);
        Optional<ShortMessageDTO> shortMessageDTO = shortMessageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shortMessageDTO);
    }

    /**
     * {@code DELETE  /short-messages/:id} : delete the "id" shortMessage.
     *
     * @param id the id of the shortMessageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    //    @DeleteMapping("/short-messages/{id}")
    public ResponseEntity<Void> deleteShortMessage(@PathVariable Long id) {
        log.debug("REST request to delete ShortMessage : {}", id);
        shortMessageService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
