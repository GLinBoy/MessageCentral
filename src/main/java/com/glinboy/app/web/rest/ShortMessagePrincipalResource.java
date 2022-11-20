package com.glinboy.app.web.rest;

import com.glinboy.app.repository.ShortMessageRepository;
import com.glinboy.app.service.ShortMessageQueryService;
import com.glinboy.app.service.ShortMessageService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ShortMessagePrincipalResource extends ShortMessageResource {

    public ShortMessagePrincipalResource(
        ShortMessageService shortMessageService,
        ShortMessageRepository shortMessageRepository,
        ShortMessageQueryService shortMessageQueryService
    ) {
        super(shortMessageService, shortMessageRepository, shortMessageQueryService);
    }
}
