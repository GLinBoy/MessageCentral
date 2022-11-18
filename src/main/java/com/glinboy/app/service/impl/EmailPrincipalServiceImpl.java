package com.glinboy.app.service.impl;

import com.glinboy.app.repository.EmailPrincipalRepository;
import com.glinboy.app.service.MailChannelService;
import com.glinboy.app.service.dto.EmailDTO;
import com.glinboy.app.service.mapper.EmailMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
@Transactional
public class EmailPrincipalServiceImpl extends EmailServiceImpl {

    public EmailPrincipalServiceImpl(
        EmailPrincipalRepository emailRepository,
        EmailMapper emailMapper,
        MailChannelService<EmailDTO> mailProviderService
    ) {
        super(emailRepository, emailMapper, mailProviderService);
    }
}
