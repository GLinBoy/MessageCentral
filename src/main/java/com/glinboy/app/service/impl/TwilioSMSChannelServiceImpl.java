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
import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;

@Service
@ConditionalOnProperty(value = "application.sms.provider", havingValue = "twilio", matchIfMissing = true)
public class TwilioSMSChannelServiceImpl extends GenericChannelServiceImpl<ShortMessageDTO>
        implements ShortMessageChannelService<ShortMessageDTO> {

    public static final String TOPIC_NAME = "TWILIO_SMSBOX";

    public final ShortMessageRepository shortMessageRepository;
    
    public final ShortMessageMapper shortMessageMapper;

    protected TwilioSMSChannelServiceImpl(JmsTemplate jmsTemplate,
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
        Twilio.init(properties.getCredential().getTwilio().getAccountSid(),
                properties.getCredential().getTwilio().getToken());
        for (var i = 0; i < shortMessageDTOs.length; i++) {
            ShortMessageDTO shortMessageDTO = shortMessageDTOs[i];
            var message = com.twilio.rest.api.v2010.account.Message
                    .creator(new PhoneNumber(shortMessageDTO.getPhoneNumber()),
                            new PhoneNumber(properties.getSms().getFrom()),
                            shortMessageDTO.getContent())
                    .create();
            log.info("SMS sent! {}", shortMessageDTO);
            log.info("SMS Result {}", message);
            this.updateStatusToSent(shortMessageDTOs);
        }
    }

    @Override
    @JmsListener(destination = TwilioSMSChannelServiceImpl.TOPIC_NAME)
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
