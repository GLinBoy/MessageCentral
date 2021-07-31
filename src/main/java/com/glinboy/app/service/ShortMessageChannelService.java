package com.glinboy.app.service;

import javax.jms.MessageListener;

import com.glinboy.app.service.dto.ShortMessageDTO;

public interface ShortMessageChannelService<T extends ShortMessageDTO> extends ChannelService<T>, MessageListener {

    void deliverMessage(T... ts) throws Exception;

}
