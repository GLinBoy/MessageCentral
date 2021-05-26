package com.glinboy.app.service;

public interface ProviderService<T> {
	
	void sendNotification(T t);

	void sendNotification(T[] ts);

}
