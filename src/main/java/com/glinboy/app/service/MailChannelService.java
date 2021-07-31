package com.glinboy.app.service;

import javax.jms.MessageListener;

import com.glinboy.app.service.dto.EmailDTO;

public interface MailChannelService<T extends EmailDTO> extends ChannelService<T>, MessageListener {

    void deliverMessage(T... ts) throws Exception;

}
