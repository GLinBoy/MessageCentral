package com.glinboy.app.service.impl;

import com.glinboy.app.repository.ShortMessageRepository;
import com.glinboy.app.service.ShortMessageChannelService;
import com.glinboy.app.service.dto.ShortMessageDTO;
import com.glinboy.app.service.mapper.ShortMessageMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
@Transactional
public class ShortMessagePrincipalServiceImpl extends ShortMessageServiceImpl {

    public ShortMessagePrincipalServiceImpl(
        ShortMessageRepository shortMessageRepository,
        ShortMessageMapper shortMessageMapper,
        ShortMessageChannelService<ShortMessageDTO> smsProvider
    ) {
        super(shortMessageRepository, shortMessageMapper, smsProvider);
    }
}
