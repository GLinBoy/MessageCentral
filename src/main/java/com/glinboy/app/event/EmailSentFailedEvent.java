package com.glinboy.app.event;

import java.util.List;

public class EmailSentFailedEvent extends MessageSentSuccessfulEvent {

	private static final long serialVersionUID = 2065902854061173261L;

	public EmailSentFailedEvent(Object source, List<Long> ids) {
		super(source, ids);
	}

}
