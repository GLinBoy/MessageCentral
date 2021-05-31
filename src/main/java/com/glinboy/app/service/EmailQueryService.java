package com.glinboy.app.service;

import com.glinboy.app.domain.*; // for static metamodels
import com.glinboy.app.domain.Email;
import com.glinboy.app.repository.EmailRepository;
import com.glinboy.app.service.criteria.EmailCriteria;
import com.glinboy.app.service.dto.EmailDTO;
import com.glinboy.app.service.mapper.EmailMapper;
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
 * Service for executing complex queries for {@link Email} entities in the database.
 * The main input is a {@link EmailCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EmailDTO} or a {@link Page} of {@link EmailDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EmailQueryService extends QueryService<Email> {

    private final Logger log = LoggerFactory.getLogger(EmailQueryService.class);

    private final EmailRepository emailRepository;

    private final EmailMapper emailMapper;

    public EmailQueryService(EmailRepository emailRepository, EmailMapper emailMapper) {
        this.emailRepository = emailRepository;
        this.emailMapper = emailMapper;
    }

    /**
     * Return a {@link List} of {@link EmailDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<EmailDTO> findByCriteria(EmailCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Email> specification = createSpecification(criteria);
        return emailMapper.toDto(emailRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link EmailDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EmailDTO> findByCriteria(EmailCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Email> specification = createSpecification(criteria);
        return emailRepository.findAll(specification, page).map(emailMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EmailCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Email> specification = createSpecification(criteria);
        return emailRepository.count(specification);
    }

    /**
     * Function to convert {@link EmailCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Email> createSpecification(EmailCriteria criteria) {
        Specification<Email> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Email_.id));
            }
            if (criteria.getReceiver() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReceiver(), Email_.receiver));
            }
            if (criteria.getSubject() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSubject(), Email_.subject));
            }
        }
        return specification;
    }

    @Transactional(readOnly = true)
    public Page<EmailDTO> findBySearch(Specification<Email> specs, Pageable page) {
        log.debug("find by specification : {}, page: {}", specs, page);
        return emailRepository.findAll(specs, page).map(emailMapper::toDto);
	}
}
