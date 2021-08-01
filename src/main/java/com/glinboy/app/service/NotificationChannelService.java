package com.glinboy.app.service;

import javax.jms.MessageListener;

import com.glinboy.app.service.dto.NotificationDTO;

public interface NotificationChannelService<T extends NotificationDTO> extends ChannelService<T>, MessageListener  {

    void deliverMessage(T... ts) throws Exception;
}
