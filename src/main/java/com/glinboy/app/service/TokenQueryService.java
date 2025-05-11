package com.glinboy.app.service;

import com.glinboy.app.domain.*; // for static metamodels
import com.glinboy.app.domain.Token;
import com.glinboy.app.repository.TokenRepository;
import com.glinboy.app.service.criteria.TokenCriteria;
import com.glinboy.app.service.dto.TokenDTO;
import com.glinboy.app.service.mapper.TokenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Token} entities in the database.
 * The main input is a {@link TokenCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TokenDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TokenQueryService extends QueryService<Token> {

    private static final Logger LOG = LoggerFactory.getLogger(TokenQueryService.class);

    private final TokenRepository tokenRepository;

    private final TokenMapper tokenMapper;

    public TokenQueryService(TokenRepository tokenRepository, TokenMapper tokenMapper) {
        this.tokenRepository = tokenRepository;
        this.tokenMapper = tokenMapper;
    }

    /**
     * Return a {@link Page} of {@link TokenDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TokenDTO> findByCriteria(TokenCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Token> specification = createSpecification(criteria);
        return tokenRepository.findAll(specification, page).map(tokenMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TokenCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Token> specification = createSpecification(criteria);
        return tokenRepository.count(specification);
    }

    /**
     * Function to convert {@link TokenCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Token> createSpecification(TokenCriteria criteria) {
        Specification<Token> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Token_.id),
                buildStringSpecification(criteria.getName(), Token_.name),
                buildStringSpecification(criteria.getToken(), Token_.token),
                buildSpecification(criteria.getDisable(), Token_.disable),
                buildRangeSpecification(criteria.getDeprecateAt(), Token_.deprecateAt),
                buildRangeSpecification(criteria.getRoles(), Token_.roles),
                buildRangeSpecification(criteria.getCreatedAt(), Token_.createdAt),
                buildStringSpecification(criteria.getCreatedBy(), Token_.createdBy),
                buildRangeSpecification(criteria.getUpdatedAt(), Token_.updatedAt),
                buildStringSpecification(criteria.getUpdatedBy(), Token_.updatedBy)
            );
        }
        return specification;
    }
}
