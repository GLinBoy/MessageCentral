package com.glinboy.app.event;

import org.springframework.context.ApplicationEvent;

import java.util.Collections;
import java.util.List;

public abstract class MessageSentSuccessfulEvent extends ApplicationEvent {

    private static final long serialVersionUID = 5734481592798974936L;
    private final List<Long> ids;

    public MessageSentSuccessfulEvent(Object source, List<Long> ids) {
        super(source);
        this.ids = ids;
    }

    public List<Long> getIds() {
        return Collections.unmodifiableList(ids);
    }
}
