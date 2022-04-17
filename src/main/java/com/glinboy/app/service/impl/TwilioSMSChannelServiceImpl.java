package com.glinboy.app.service.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.service.dto.ShortMessageDTO;
import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;

@Service
@ConditionalOnProperty(value = "application.sms.provider", havingValue = "twilio", matchIfMissing = true)
public class TwilioSMSChannelServiceImpl extends AbstractShortMessageChannelServiceImpl {

    protected TwilioSMSChannelServiceImpl(ApplicationEventPublisher publisher,
            ApplicationProperties properties) {
        super(publisher, properties);
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
        }
    }

}
