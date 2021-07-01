package com.glinboy.app.service;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.glinboy.app.service.dto.ShortMessageDTO;
import com.glinboy.app.service.dto.ShortMessagesDTO;

/**
 * Service Interface for managing {@link com.glinboy.app.domain.ShortMessage}.
 */
public interface ShortMessageService {
    /**
     * Save a shortMessage.
     *
     * @param shortMessageDTO the entity to save.
     * @return the persisted entity.
     */
    ShortMessageDTO save(ShortMessageDTO shortMessageDTO);

    /**
     * Partially updates a shortMessage.
     *
     * @param shortMessageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ShortMessageDTO> partialUpdate(ShortMessageDTO shortMessageDTO);

    /**
     * Get all the shortMessages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ShortMessageDTO> findAll(Pageable pageable);

    /**
     * Get the "id" shortMessage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ShortMessageDTO> findOne(Long id);

    /**
     * Delete the "id" shortMessage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    List<ShortMessageDTO> save(@Valid List<ShortMessagesDTO> shortMessagesDTO);

}
