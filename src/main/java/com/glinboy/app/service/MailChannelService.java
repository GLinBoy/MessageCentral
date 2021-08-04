package com.glinboy.app.service;

import com.glinboy.app.service.dto.EmailDTO;

public interface MailChannelService<T extends EmailDTO> extends ChannelService<T> {

    void deliverMessage(T... ts) throws Exception;

}
