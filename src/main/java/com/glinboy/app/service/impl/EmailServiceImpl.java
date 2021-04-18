package com.glinboy.app.service.impl;

import com.glinboy.app.domain.Email;
import com.glinboy.app.repository.EmailRepository;
import com.glinboy.app.service.EmailService;
import com.glinboy.app.service.MailProviderService;
import com.glinboy.app.service.dto.EmailDTO;
import com.glinboy.app.service.mapper.EmailMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Email}.
 */
@Service
@Transactional
public class EmailServiceImpl implements EmailService {

	private final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

	private final EmailRepository emailRepository;

	private final EmailMapper emailMapper;

	private final MailProviderService<EmailDTO> mailProviderService;

	public EmailServiceImpl(EmailRepository emailRepository,
			EmailMapper emailMapper,
			MailProviderService<EmailDTO> mailProviderService) {
		this.emailRepository = emailRepository;
		this.emailMapper = emailMapper;
		this.mailProviderService = mailProviderService;
	}

	@Override
	public EmailDTO save(EmailDTO emailDTO) {
		log.debug("Request to save Email : {}", emailDTO);
		Email email = emailMapper.toEntity(emailDTO);
		email = emailRepository.save(email);
		EmailDTO e = emailMapper.toDto(email);
		mailProviderService.sendEmail(e);
		return e;
	}

	@Override
	public Optional<EmailDTO> partialUpdate(EmailDTO emailDTO) {
		log.debug("Request to partially update Email : {}", emailDTO);

		return emailRepository.findById(emailDTO.getId()).map(existingEmail -> {
			emailMapper.partialUpdate(existingEmail, emailDTO);
			return existingEmail;
		}).map(emailRepository::save).map(emailMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EmailDTO> findAll(Pageable pageable) {
		log.debug("Request to get all Emails");
		return emailRepository.findAll(pageable).map(emailMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<EmailDTO> findOne(Long id) {
		log.debug("Request to get Email : {}", id);
		return emailRepository.findById(id).map(emailMapper::toDto);
	}

	@Override
	public void delete(Long id) {
		log.debug("Request to delete Email : {}", id);
		emailRepository.deleteById(id);
	}
}
