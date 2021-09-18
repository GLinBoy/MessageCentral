package com.glinboy.app.service.impl;

import com.glinboy.app.domain.ShortMessage;
import com.glinboy.app.repository.ShortMessageRepository;
import com.glinboy.app.service.ShortMessageService;
import com.glinboy.app.service.dto.ShortMessageDTO;
import com.glinboy.app.service.mapper.ShortMessageMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ShortMessage}.
 */
@Service
@Transactional
public class ShortMessageServiceImpl implements ShortMessageService {

    private final Logger log = LoggerFactory.getLogger(ShortMessageServiceImpl.class);

    private final ShortMessageRepository shortMessageRepository;

    private final ShortMessageMapper shortMessageMapper;

    public ShortMessageServiceImpl(ShortMessageRepository shortMessageRepository, ShortMessageMapper shortMessageMapper) {
        this.shortMessageRepository = shortMessageRepository;
        this.shortMessageMapper = shortMessageMapper;
    }

    @Override
    public ShortMessageDTO save(ShortMessageDTO shortMessageDTO) {
        log.debug("Request to save ShortMessage : {}", shortMessageDTO);
        ShortMessage shortMessage = shortMessageMapper.toEntity(shortMessageDTO);
        shortMessage = shortMessageRepository.save(shortMessage);
        return shortMessageMapper.toDto(shortMessage);
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
}
