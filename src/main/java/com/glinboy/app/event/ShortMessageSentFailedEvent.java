package com.glinboy.app.event;

import java.util.List;

public class ShortMessageSentFailedEvent extends MessageSentSuccessfulEvent {

	private static final long serialVersionUID = 2065902854061173261L;

	public ShortMessageSentFailedEvent(Object source, List<Long> ids) {
		super(source, ids);
	}

}
