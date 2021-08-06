package com.glinboy.app.service;

import com.glinboy.app.service.dto.ShortMessageDTO;

public interface ShortMessageChannelService<T extends ShortMessageDTO> extends ChannelService<T> {

    void deliverMessage(T... ts) throws Exception;
    void onMessage(T... ts);

}
