package com.glinboy.app.service.impl;

import com.glinboy.app.domain.ShortMessage;
import com.glinboy.app.domain.enumeration.MessageStatus;
import com.glinboy.app.event.ShortMessageSentFailedEvent;
import com.glinboy.app.event.ShortMessageSentSuccessfulEvent;
import com.glinboy.app.repository.ShortMessageRepository;
import com.glinboy.app.service.ShortMessageChannelService;
import com.glinboy.app.service.dto.ShortMessageDTO;
import com.glinboy.app.service.dto.ShortMessagesDTO;
import com.glinboy.app.service.mapper.ShortMessageMapper;
import com.glinboy.app.util.Patterns;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
@Transactional
public class ShortMessagePrincipalServiceImpl extends ShortMessageServiceImpl {

    private final Logger log = LoggerFactory.getLogger(ShortMessagePrincipalServiceImpl.class);

    private final ShortMessageRepository shortMessageRepository;

    private final ShortMessageMapper shortMessageMapper;

    private final ShortMessageChannelService<ShortMessageDTO> smsProvider;

    public ShortMessagePrincipalServiceImpl(
        ShortMessageRepository shortMessageRepository,
        ShortMessageMapper shortMessageMapper,
        ShortMessageChannelService<ShortMessageDTO> smsProvider
    ) {
        super(shortMessageRepository, shortMessageMapper, smsProvider);
        this.shortMessageRepository = shortMessageRepository;
        this.shortMessageMapper = shortMessageMapper;
        this.smsProvider = smsProvider;
    }

    @Override
    public ShortMessageDTO save(ShortMessageDTO shortMessageDTO) {
        log.debug("Request to save ShortMessage : {}", shortMessageDTO);
        ShortMessage shortMessage = shortMessageMapper.toEntity(shortMessageDTO);
        if (shortMessage.getId() == null) {
            shortMessage.setStatus(MessageStatus.IN_QUEUE);
        }
        shortMessage = shortMessageRepository.save(shortMessage);
        ShortMessageDTO smsDTO = shortMessageMapper.toDto(shortMessage);
        smsProvider.sendMessage(smsDTO);
        return smsDTO;
    }

    @Override
    public List<ShortMessageDTO> save(List<ShortMessagesDTO> shortMessagesDTO) {
        log.debug("Request to save ShortMessage : {}", shortMessagesDTO);
        List<ShortMessage> messages = shortMessagesDTO
            .stream()
            .flatMap(ss ->
                Set
                    .copyOf(ss.getPhoneNumbers())
                    .stream()
                    .filter(p -> p.matches(Patterns.SMS_PATTERN))
                    .map(p -> {
                        ShortMessage s = new ShortMessage();
                        s.phoneNumber(p);
                        s.setContent(ss.getContent());
                        s.setStatus(MessageStatus.IN_QUEUE);
                        return s;
                    })
            )
            .collect(Collectors.toList());
        log.info("List of {} SMSs: {}", messages.size(), messages);
        messages = this.shortMessageRepository.saveAll(messages);
        List<ShortMessageDTO> dtoList = this.shortMessageMapper.toDto(messages);
        this.smsProvider.sendMessage(dtoList.toArray(new ShortMessageDTO[dtoList.size()]));
        return dtoList;
    }

    @Transactional
    @EventListener
    public void onMessageSent(ShortMessageSentSuccessfulEvent event) {
        this.shortMessageRepository.updateStatus(MessageStatus.SENT, event.getIds().toArray(new Long[event.getIds().size()]));
    }

    @Transactional
    @EventListener
    public void onMessageFailed(ShortMessageSentFailedEvent event) {
        this.shortMessageRepository.updateStatus(MessageStatus.FAILED, event.getIds().toArray(new Long[event.getIds().size()]));
    }
}
