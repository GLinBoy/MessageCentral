package com.glinboy.app.service.impl;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.repository.ShortMessageRepository;
import com.glinboy.app.service.ShortMessageChannelService;
import com.glinboy.app.service.dto.ShortMessageDTO;
import com.glinboy.app.service.mapper.ShortMessageMapper;
import com.kavenegar.sdk.KavenegarApi;
import com.kavenegar.sdk.excepctions.ApiException;
import com.kavenegar.sdk.excepctions.HttpException;
import com.kavenegar.sdk.models.SendResult;

@Service
@ConditionalOnProperty(value = "application.sms.provider", havingValue = "kavenegar")
public class KavenegarSMSChannelServiceImpl extends GenericChannelServiceImpl<ShortMessageDTO>
        implements ShortMessageChannelService<ShortMessageDTO> {

    public static final String TOPIC_NAME = "KAVENEGAR_SMSBOX";

    private final ShortMessageRepository shortMessageRepository;
    
    private final ShortMessageMapper shortMessageMapper;

    protected KavenegarSMSChannelServiceImpl(JmsTemplate jmsTemplate,
            ApplicationProperties properties,
            ShortMessageRepository shortMessageRepository,
            ShortMessageMapper shortMessageMapper) {
        super(jmsTemplate, properties);
        this.shortMessageRepository = shortMessageRepository;
        this.shortMessageMapper = shortMessageMapper;
    }

    @Override
    String getTopicName() {
        return TOPIC_NAME;
    }
    
    @Override
    public Consumer<ShortMessageDTO[]> saveFunction() {
        return emails -> shortMessageRepository.saveAll(Stream
                .of(emails)
                .map(shortMessageMapper::toEntity)
                .collect(Collectors.toList()));
    }

    @Override
    public void deliverMessage(ShortMessageDTO... shortMessageDTOs) {
        var api = new KavenegarApi(properties.getCredential().getKavenegar().getToken());
        for (var i = 0; i < shortMessageDTOs.length; i++) {
            ShortMessageDTO shortMessageDTO = shortMessageDTOs[i];
            try {
                SendResult result = api.send(properties.getSms().getFrom(), shortMessageDTO.getPhoneNumber(),
                        shortMessageDTO.getContent());
                log.info("SMS sent! {}", shortMessageDTO);
                log.info("SMS Result {}", result);
                updateStatusToSent(shortMessageDTOs);
            } catch (HttpException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
                log.error("HttpException: {}", ex.getMessage(), ex);
            } catch (ApiException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
                log.error("ApiException: {}", ex.getMessage(), ex);
            }
        }
    }

    @Override
    @JmsListener(destination = KavenegarSMSChannelServiceImpl.TOPIC_NAME)
    public void onMessage(Message message) {
        try {
            var objectMessage = (ObjectMessage) message;
            var shortMessageDTO = (ShortMessageDTO) objectMessage.getObject();
            this.deliverMessage(shortMessageDTO);
        } catch (JMSException e) {
            log.error("Parsing message failed: {}", e.getMessage(), e);
        }
    }

}
