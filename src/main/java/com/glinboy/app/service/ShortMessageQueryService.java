package com.glinboy.app.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// for static metamodels
import com.glinboy.app.domain.ShortMessage;
import com.glinboy.app.domain.ShortMessage_;
import com.glinboy.app.repository.ShortMessageRepository;
import com.glinboy.app.service.criteria.ShortMessageCriteria;
import com.glinboy.app.service.dto.ShortMessageDTO;
import com.glinboy.app.service.mapper.ShortMessageMapper;

import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ShortMessage} entities in the database.
 * The main input is a {@link ShortMessageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ShortMessageDTO} or a {@link Page} of {@link ShortMessageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ShortMessageQueryService extends QueryService<ShortMessage> {

    private final Logger log = LoggerFactory.getLogger(ShortMessageQueryService.class);

    private final ShortMessageRepository shortMessageRepository;

    private final ShortMessageMapper shortMessageMapper;

    public ShortMessageQueryService(ShortMessageRepository shortMessageRepository, ShortMessageMapper shortMessageMapper) {
        this.shortMessageRepository = shortMessageRepository;
        this.shortMessageMapper = shortMessageMapper;
    }

    /**
     * Return a {@link List} of {@link ShortMessageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ShortMessageDTO> findByCriteria(ShortMessageCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ShortMessage> specification = createSpecification(criteria);
        return shortMessageMapper.toDto(shortMessageRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ShortMessageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ShortMessageDTO> findByCriteria(ShortMessageCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ShortMessage> specification = createSpecification(criteria);
        return shortMessageRepository.findAll(specification, page).map(shortMessageMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ShortMessageCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ShortMessage> specification = createSpecification(criteria);
        return shortMessageRepository.count(specification);
    }

    /**
     * Return the number of matching entities in the database.
     * @param specification The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countBySpecification(Specification<ShortMessage> specs) {
        log.debug("count by specification: {}", specs);
        return shortMessageRepository.count(specs);
    }

    /**
     * Function to convert {@link ShortMessageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ShortMessage> createSpecification(ShortMessageCriteria criteria) {
        Specification<ShortMessage> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ShortMessage_.id));
            }
            if (criteria.getPhoneNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhoneNumber(), ShortMessage_.phoneNumber));
            }
            if (criteria.getContent() != null) {
                specification = specification.and(buildStringSpecification(criteria.getContent(), ShortMessage_.content));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), ShortMessage_.status));
            }
        }
        return specification;
    }

    @Transactional(readOnly = true)
    public Page<ShortMessageDTO> findBySearch(Specification<ShortMessage> specs, Pageable page) {
        log.debug("find by specification : {}, page: {}", specs, page);
        return shortMessageRepository.findAll(specs, page).map(shortMessageMapper::toDto);
	}
}
