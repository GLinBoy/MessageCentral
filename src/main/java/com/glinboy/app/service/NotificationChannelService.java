package com.glinboy.app.service;

import com.glinboy.app.service.dto.NotificationDTO;

public interface NotificationChannelService<T extends NotificationDTO> extends ChannelService<T> {

    void deliverMessage(T... ts) throws Exception;
    void onMessage(T... ts);
}
