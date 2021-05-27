package com.glinboy.app.service;

public interface ChannelService<T> {

	void sendMessage(T... ts);

}
