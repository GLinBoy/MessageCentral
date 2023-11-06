package com.glinboy.app.service;

import com.glinboy.app.domain.*; // for static metamodels
import com.glinboy.app.domain.Token;
import com.glinboy.app.repository.TokenRepository;
import com.glinboy.app.service.criteria.TokenCriteria;
import com.glinboy.app.service.dto.TokenDTO;
import com.glinboy.app.service.mapper.TokenMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
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
 * It returns a {@link List} of {@link TokenDTO} or a {@link Page} of {@link TokenDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TokenQueryService extends QueryService<Token> {

    private final Logger log = LoggerFactory.getLogger(TokenQueryService.class);

    private final TokenRepository tokenRepository;

    private final TokenMapper tokenMapper;

    public TokenQueryService(TokenRepository tokenRepository, TokenMapper tokenMapper) {
        this.tokenRepository = tokenRepository;
        this.tokenMapper = tokenMapper;
    }

    /**
     * Return a {@link List} of {@link TokenDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TokenDTO> findByCriteria(TokenCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Token> specification = createSpecification(criteria);
        return tokenMapper.toDto(tokenRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TokenDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TokenDTO> findByCriteria(TokenCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
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
        log.debug("count by criteria : {}", criteria);
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
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Token_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Token_.name));
            }
            if (criteria.getToken() != null) {
                specification = specification.and(buildStringSpecification(criteria.getToken(), Token_.token));
            }
            if (criteria.getDisable() != null) {
                specification = specification.and(buildSpecification(criteria.getDisable(), Token_.disable));
            }
            if (criteria.getDeprecateAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDeprecateAt(), Token_.deprecateAt));
            }
            if (criteria.getRoles() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRoles(), Token_.roles));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Token_.createdAt));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Token_.createdBy));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), Token_.updatedAt));
            }
            if (criteria.getUpdatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUpdatedBy(), Token_.updatedBy));
            }
        }
        return specification;
    }
}
