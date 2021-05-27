package com.glinboy.app.service;

public interface ProviderService<T> {
	
	void sendMessage(T t);

	void sendMessage(T... ts);

}
