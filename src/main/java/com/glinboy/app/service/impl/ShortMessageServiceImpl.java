package com.glinboy.app.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glinboy.app.domain.ShortMessage;
import com.glinboy.app.domain.enumeration.MessageStatus;
import com.glinboy.app.repository.ShortMessageRepository;
import com.glinboy.app.service.ShortMessageChannelService;
import com.glinboy.app.service.ShortMessageService;
import com.glinboy.app.service.dto.ShortMessageDTO;
import com.glinboy.app.service.dto.ShortMessagesDTO;
import com.glinboy.app.service.mapper.ShortMessageMapper;
import com.glinboy.app.util.Patterns;

/**
 * Service Implementation for managing {@link ShortMessage}.
 */
@Service
@Transactional
public class ShortMessageServiceImpl implements ShortMessageService {

    private final Logger log = LoggerFactory.getLogger(ShortMessageServiceImpl.class);

    private final ShortMessageRepository shortMessageRepository;

    private final ShortMessageMapper shortMessageMapper;

    private final ShortMessageChannelService<ShortMessageDTO> smsProvider;

    public ShortMessageServiceImpl(ShortMessageRepository shortMessageRepository,
            ShortMessageMapper shortMessageMapper,
            ShortMessageChannelService<ShortMessageDTO> smsProvider) {
        this.shortMessageRepository = shortMessageRepository;
        this.shortMessageMapper = shortMessageMapper;
        this.smsProvider = smsProvider;
    }

    @Override
    public ShortMessageDTO save(ShortMessageDTO shortMessageDTO) {
        log.debug("Request to save ShortMessage : {}", shortMessageDTO);
        ShortMessage shortMessage = shortMessageMapper.toEntity(shortMessageDTO);
        if(shortMessage.getId() == null) {
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
        List<ShortMessage> messages = shortMessagesDTO.stream()
                .flatMap( ss -> Set.copyOf(ss.getPhoneNumbers())
                .stream().filter(p -> p.matches(Patterns.SMS_PATTERN))
                .map(p -> {
                    ShortMessage s = new ShortMessage();
                    s.phoneNumber(p);
                    s.setContent(ss.getContent());
                    s.setStatus(MessageStatus.IN_QUEUE);
                    return s;
                    }))
                .collect(Collectors.toList());
        log.info("List of {} SMSs: {}", messages.size(), messages);
        messages = this.shortMessageRepository.saveAll(messages);
        List<ShortMessageDTO> dtoList = this.shortMessageMapper.toDto(messages);
        this.smsProvider.sendMessage(dtoList.toArray(new ShortMessageDTO[dtoList.size()]));
        return dtoList;
    }

    @Override
    public Optional<ShortMessageDTO> partialUpdate(ShortMessageDTO shortMessageDTO) {
        log.debug("Request to partially update ShortMessage : {}", shortMessageDTO);

        return shortMessageRepository
            .findById(shortMessageDTO.getId())
            .map(
                existingShortMessage -> {
                    shortMessageMapper.partialUpdate(existingShortMessage, shortMessageDTO);

                    return existingShortMessage;
                }
            )
            .map(shortMessageRepository::save)
            .map(shortMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShortMessageDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ShortMessages");
        return shortMessageRepository.findAll(pageable).map(shortMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShortMessageDTO> findOne(Long id) {
        log.debug("Request to get ShortMessage : {}", id);
        return shortMessageRepository.findById(id).map(shortMessageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ShortMessage : {}", id);
        shortMessageRepository.deleteById(id);
    }

    @Transactional
    @JmsListener(destination = ShortMessageService.TOPIC_NAME_SENT)
    public void onMessageSent(Long... ids) {
        this.shortMessageRepository.updateStatus(MessageStatus.SENT, ids);
    }

    @Transactional
    @JmsListener(destination = ShortMessageService.TOPIC_NAME_FAILED)
    public void onMessageFailed(Long... ids) {
        this.shortMessageRepository.updateStatus(MessageStatus.FAILED, ids);
    }
}
