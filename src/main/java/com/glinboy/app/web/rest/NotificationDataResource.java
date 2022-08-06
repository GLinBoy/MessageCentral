package com.glinboy.app.web.rest;

import com.glinboy.app.domain.NotificationData;
import com.glinboy.app.repository.NotificationDataRepository;
import com.glinboy.app.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.glinboy.app.domain.NotificationData}.
 */
//@RestController
//@RequestMapping("/api")
@Transactional
public class NotificationDataResource {

    private static final String ENTITY_NAME = "notificationData";
    private final Logger log = LoggerFactory.getLogger(NotificationDataResource.class);
    private final NotificationDataRepository notificationDataRepository;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public NotificationDataResource(NotificationDataRepository notificationDataRepository) {
        this.notificationDataRepository = notificationDataRepository;
    }

    /**
     * {@code POST  /notification-data} : Create a new notificationData.
     *
     * @param notificationData the notificationData to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notificationData, or with status {@code 400 (Bad Request)} if the notificationData has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/notification-data")
    public ResponseEntity<NotificationData> createNotificationData(@Valid @RequestBody NotificationData notificationData)
        throws URISyntaxException {
        log.debug("REST request to save NotificationData : {}", notificationData);
        if (notificationData.getId() != null) {
            throw new BadRequestAlertException("A new notificationData cannot already have an ID", ENTITY_NAME, "idexists");
        }
        NotificationData result = notificationDataRepository.save(notificationData);
        return ResponseEntity
            .created(new URI("/api/notification-data/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /notification-data/:id} : Updates an existing notificationData.
     *
     * @param id               the id of the notificationData to save.
     * @param notificationData the notificationData to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationData,
     * or with status {@code 400 (Bad Request)} if the notificationData is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notificationData couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/notification-data/{id}")
    public ResponseEntity<NotificationData> updateNotificationData(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NotificationData notificationData
    ) throws URISyntaxException {
        log.debug("REST request to update NotificationData : {}, {}", id, notificationData);
        if (notificationData.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationData.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        NotificationData result = notificationDataRepository.save(notificationData);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationData.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /notification-data/:id} : Partial updates given fields of an existing notificationData, field will ignore if it is null
     *
     * @param id               the id of the notificationData to save.
     * @param notificationData the notificationData to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationData,
     * or with status {@code 400 (Bad Request)} if the notificationData is not valid,
     * or with status {@code 404 (Not Found)} if the notificationData is not found,
     * or with status {@code 500 (Internal Server Error)} if the notificationData couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/notification-data/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NotificationData> partialUpdateNotificationData(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NotificationData notificationData
    ) throws URISyntaxException {
        log.debug("REST request to partial update NotificationData partially : {}, {}", id, notificationData);
        if (notificationData.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationData.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NotificationData> result = notificationDataRepository
            .findById(notificationData.getId())
            .map(existingNotificationData -> {
                if (notificationData.getDataKey() != null) {
                    existingNotificationData.setDataKey(notificationData.getDataKey());
                }
                if (notificationData.getDataValue() != null) {
                    existingNotificationData.setDataValue(notificationData.getDataValue());
                }

                return existingNotificationData;
            })
            .map(notificationDataRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationData.getId().toString())
        );
    }

    /**
     * {@code GET  /notification-data} : get all the notificationData.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notificationData in body.
     */
    @GetMapping("/notification-data")
    public List<NotificationData> getAllNotificationData() {
        log.debug("REST request to get all NotificationData");
        return notificationDataRepository.findAll();
    }

    /**
     * {@code GET  /notification-data/:id} : get the "id" notificationData.
     *
     * @param id the id of the notificationData to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificationData, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/notification-data/{id}")
    public ResponseEntity<NotificationData> getNotificationData(@PathVariable Long id) {
        log.debug("REST request to get NotificationData : {}", id);
        Optional<NotificationData> notificationData = notificationDataRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(notificationData);
    }

    /**
     * {@code DELETE  /notification-data/:id} : delete the "id" notificationData.
     *
     * @param id the id of the notificationData to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/notification-data/{id}")
    public ResponseEntity<Void> deleteNotificationData(@PathVariable Long id) {
        log.debug("REST request to delete NotificationData : {}", id);
        notificationDataRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
