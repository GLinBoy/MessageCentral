package com.glinboy.app.service.impl;

import com.glinboy.app.domain.Email;
import com.glinboy.app.domain.enumeration.MessageStatus;
import com.glinboy.app.event.EmailSentFailedEvent;
import com.glinboy.app.event.EmailSentSuccessfulEvent;
import com.glinboy.app.repository.EmailPrincipalRepository;
import com.glinboy.app.repository.EmailRepository;
import com.glinboy.app.service.MailChannelService;
import com.glinboy.app.service.dto.EmailDTO;
import com.glinboy.app.service.dto.EmailsDTO;
import com.glinboy.app.service.mapper.EmailMapper;
import com.glinboy.app.util.Patterns;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
@Transactional
public class EmailPrincipalServiceImpl extends EmailServiceImpl {

    private final Logger log = LoggerFactory.getLogger(EmailPrincipalServiceImpl.class);

    private final EmailRepository emailRepository;

    private final EmailMapper emailMapper;

    private final MailChannelService<EmailDTO> mailProviderService;

    public EmailPrincipalServiceImpl(
        EmailPrincipalRepository emailRepository,
        EmailMapper emailMapper,
        MailChannelService<EmailDTO> mailProviderService
    ) {
        super(emailRepository, emailMapper, mailProviderService);
        this.emailRepository = emailRepository;
        this.emailMapper = emailMapper;
        this.mailProviderService = mailProviderService;
    }

    @Override
    public EmailDTO save(EmailDTO emailDTO) {
        log.debug("Request to save Email : {}", emailDTO);
        var email = emailMapper.toEntity(emailDTO);
        if (email.getId() == null) {
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
        var emails = emailsDTO
            .stream()
            .flatMap(es ->
                Set
                    .copyOf(es.getReceivers())
                    .stream()
                    .filter(r -> r.matches(Patterns.EMAIL_PATTERN))
                    .map(r -> {
                        var e = new Email();
                        e.setReceiver(r);
                        e.setSubject(es.getSubject());
                        e.setContent(es.getContent());
                        e.setStatus(MessageStatus.IN_QUEUE);
                        e.setEmailType(es.getEmailType());
                        return e;
                    })
            )
            .toList();
        log.info("List of {} Emails: {}", emails.size(), emails);
        emails = this.emailRepository.saveAll(emails);
        var dtoList = this.emailMapper.toDto(emails);
        this.mailProviderService.sendMessage(dtoList.toArray(new EmailDTO[dtoList.size()]));
        return dtoList;
    }

    @Transactional
    @EventListener
    public void onMessageSent(EmailSentSuccessfulEvent event) {
        this.emailRepository.updateStatus(MessageStatus.SENT, event.getIds().toArray(new Long[event.getIds().size()]));
    }

    @Transactional
    @EventListener
    public void onMessageFailed(EmailSentFailedEvent event) {
        this.emailRepository.updateStatus(MessageStatus.FAILED, event.getIds().toArray(new Long[event.getIds().size()]));
    }
}
