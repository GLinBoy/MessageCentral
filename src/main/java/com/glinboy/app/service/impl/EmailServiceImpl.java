package com.glinboy.app.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glinboy.app.domain.Email;
import com.glinboy.app.domain.enumeration.MessageStatus;
import com.glinboy.app.event.EmailSentFailedEvent;
import com.glinboy.app.event.EmailSentSuccessfulEvent;
import com.glinboy.app.repository.EmailRepository;
import com.glinboy.app.service.EmailService;
import com.glinboy.app.service.MailChannelService;
import com.glinboy.app.service.dto.EmailDTO;
import com.glinboy.app.service.dto.EmailsDTO;
import com.glinboy.app.service.mapper.EmailMapper;
import com.glinboy.app.util.Patterns;

/**
 * Service Implementation for managing {@link Email}.
 */
@Service
@Transactional
public class EmailServiceImpl implements EmailService {

    private final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final EmailRepository emailRepository;

    private final EmailMapper emailMapper;

    private final MailChannelService<EmailDTO> mailProviderService;

    public EmailServiceImpl(EmailRepository emailRepository, EmailMapper emailMapper,
            MailChannelService<EmailDTO> mailProviderService) {
        this.emailRepository = emailRepository;
        this.emailMapper = emailMapper;
        this.mailProviderService = mailProviderService;
    }

    @Override
    public EmailDTO save(EmailDTO emailDTO) {
        log.debug("Request to save Email : {}", emailDTO);
        var email = emailMapper.toEntity(emailDTO);
        if(email.getId() == null) {
            email.setStatus(MessageStatus.IN_QUEUE);
        }
        email = emailRepository.save(email);
        EmailDTO e = emailMapper.toDto(email);
        mailProviderService.sendMessage(e);
        return e;
    }

    @Override
    public List<EmailDTO> save(List<EmailsDTO> emailsDTO) {
        log.debug("Request to save Emails : {}", emailsDTO);
        var emails = emailsDTO.stream().flatMap(
                es -> Set.copyOf(es.getReceivers()).stream().filter(r -> r.matches(Patterns.EMAIL_PATTERN)).map(r -> {
                    var e = new Email();
                    e.setReceiver(r);
                    e.setSubject(es.getSubject());
                    e.setContent(es.getContent());
                    e.setStatus(MessageStatus.IN_QUEUE);
                    return e;
                })).collect(Collectors.toList());
        log.info("List of {} Emails: {}", emails.size(), emails);
        emails = this.emailRepository.saveAll(emails);
        var dtoList = this.emailMapper.toDto(emails);
        this.mailProviderService.sendMessage(dtoList.toArray(new EmailDTO[dtoList.size()]));
        return dtoList;
    }

    @Override
    public EmailDTO update(EmailDTO emailDTO) {
        log.debug("Request to save Email : {}", emailDTO);
        Email email = emailMapper.toEntity(emailDTO);
        email = emailRepository.save(email);
        return emailMapper.toDto(email);
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

    @Transactional
    @EventListener()
    public void onMessageSent(EmailSentSuccessfulEvent event) {
        this.emailRepository.updateStatus(MessageStatus.SENT, event.getIds().toArray(new Long[event.getIds().size()]));
    }

    @Transactional
    public void onMessageFailed(EmailSentFailedEvent event) {
        this.emailRepository.updateStatus(MessageStatus.FAILED, event.getIds().toArray(new Long[event.getIds().size()]));
    }
}
