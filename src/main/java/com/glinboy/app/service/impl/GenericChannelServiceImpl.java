package com.glinboy.app.service.impl;

import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.service.ChannelService;

public abstract class GenericChannelServiceImpl<T> implements ChannelService<T>, MessageListener{
	
	protected final Logger log;
	
	protected final ApplicationProperties properties;
	
	protected final JmsTemplate jmsTemplate;
	
	protected GenericChannelServiceImpl(JmsTemplate jmsTemplate,
			ApplicationProperties properties) {
		log = LoggerFactory.getLogger(this.getClass());
		this.jmsTemplate = jmsTemplate;
		this.properties = properties;
	}
	
	public abstract String getTopicName();
	
	public abstract void deliverMessage(T... ts);

	@Override
	public void sendMessage(T... ts) {
		for(var i = 0; i < ts.length; i++) {
			jmsTemplate.convertAndSend(getTopicName(), ts[i]);
		}
	}

}
