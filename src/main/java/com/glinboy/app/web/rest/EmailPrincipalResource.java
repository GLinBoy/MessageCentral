package com.glinboy.app.web.rest;

import com.glinboy.app.repository.EmailRepository;
import com.glinboy.app.service.EmailQueryService;
import com.glinboy.app.service.EmailService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EmailPrincipalResource extends EmailResource {

    public EmailPrincipalResource(EmailService emailService, EmailRepository emailRepository, EmailQueryService emailQueryService) {
        super(emailService, emailRepository, emailQueryService);
    }
}
