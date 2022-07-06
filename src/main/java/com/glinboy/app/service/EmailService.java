package com.glinboy.app.service;

import com.glinboy.app.service.dto.EmailDTO;
import com.glinboy.app.service.dto.EmailsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.glinboy.app.domain.Email}.
 */
public interface EmailService {

    /**
     * Save a email.
     *
     * @param emailDTO the entity to save.
     * @return the persisted entity.
     */
    EmailDTO save(EmailDTO emailDTO);

    /**
     * Updates a email.
     *
     * @param emailDTO the entity to update.
     * @return the persisted entity.
     */
    EmailDTO update(EmailDTO emailDTO);

    /**
     * Partially updates a email.
     *
     * @param emailDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<EmailDTO> partialUpdate(EmailDTO emailDTO);

    /**
     * Get all the emails.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EmailDTO> findAll(Pageable pageable);

    /**
     * Get the "id" email.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EmailDTO> findOne(Long id);

    /**
     * Delete the "id" email.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    List<EmailDTO> save(@Valid List<EmailsDTO> emailsDTO);
}
