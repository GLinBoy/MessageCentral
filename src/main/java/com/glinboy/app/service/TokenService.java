package com.glinboy.app.service;

import com.glinboy.app.service.dto.TokenDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.glinboy.app.domain.Token}.
 */
public interface TokenService {
    /**
     * Save a token.
     *
     * @param tokenDTO the entity to save.
     * @return the persisted entity.
     */
    TokenDTO save(TokenDTO tokenDTO);

    /**
     * Updates a token.
     *
     * @param tokenDTO the entity to update.
     * @return the persisted entity.
     */
    TokenDTO update(TokenDTO tokenDTO);

    /**
     * Partially updates a token.
     *
     * @param tokenDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TokenDTO> partialUpdate(TokenDTO tokenDTO);

    /**
     * Get the "id" token.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TokenDTO> findOne(Long id);

    /**
     * Delete the "id" token.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
