package com.glinboy.app.service;

import java.util.function.Consumer;
import java.util.stream.Stream;

import com.glinboy.app.domain.enumeration.MessageStatus;
import com.glinboy.app.service.dto.EmailDTO;

public interface MailChannelService<T extends EmailDTO> extends ChannelService<T> {

    Consumer<T[]> saveFunction();

    @Override
    default void updateStatusToSent(T... ts) {
        Stream.of(ts).forEach(t -> t.setStatus(MessageStatus.SENT));
        saveFunction().accept(ts);
    }
}
