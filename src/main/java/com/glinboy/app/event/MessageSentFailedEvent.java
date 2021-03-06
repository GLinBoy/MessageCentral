package com.glinboy.app.event;

import org.springframework.context.ApplicationEvent;

import java.util.Collections;
import java.util.List;

public abstract class MessageSentFailedEvent extends ApplicationEvent {

    private static final long serialVersionUID = -5511582251535055160L;
    private final List<Long> ids;

    public MessageSentFailedEvent(Object source, List<Long> ids) {
        super(source);
        this.ids = ids;
    }

    public List<Long> getIds() {
        return Collections.unmodifiableList(ids);
    }
}
