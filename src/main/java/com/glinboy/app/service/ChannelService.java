package com.glinboy.app.service;

public interface ProviderService<T> {

	void sendMessage(T... ts);

}
