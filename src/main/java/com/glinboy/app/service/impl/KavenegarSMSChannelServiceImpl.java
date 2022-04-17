package com.glinboy.app.service.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.service.dto.ShortMessageDTO;
import com.kavenegar.sdk.KavenegarApi;
import com.kavenegar.sdk.excepctions.ApiException;
import com.kavenegar.sdk.excepctions.HttpException;
import com.kavenegar.sdk.models.SendResult;

@Service
@ConditionalOnProperty(value = "application.sms.provider", havingValue = "kavenegar")
public class KavenegarSMSChannelServiceImpl extends AbstractShortMessageChannelServiceImpl {

    protected KavenegarSMSChannelServiceImpl(ApplicationEventPublisher publisher,
            ApplicationProperties properties) {
        super(publisher, properties);
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
            } catch (HttpException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
                log.error("HttpException: {}", ex.getMessage(), ex);
            } catch (ApiException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
                log.error("ApiException: {}", ex.getMessage(), ex);
            }
        }
    }

}
