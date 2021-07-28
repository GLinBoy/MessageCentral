package com.glinboy.app.service;

import java.util.function.Consumer;
import java.util.stream.Stream;

import com.glinboy.app.domain.enumeration.MessageStatus;
import com.glinboy.app.service.dto.NotificationDTO;

public interface NotificationChannelService<T extends NotificationDTO> extends ChannelService<T> {

    Consumer<T[]> saveFunction();

    default void updateStatusToSent(T... ts) {
        Stream.of(ts).forEach(t -> t.setStatus(MessageStatus.SENT));
        saveFunction().accept(ts);
    }

    default void updateStatusToFailed(T... ts) {
        Stream.of(ts).forEach(t -> t.setStatus(MessageStatus.FAILED));
        saveFunction().accept(ts);
    }
}
