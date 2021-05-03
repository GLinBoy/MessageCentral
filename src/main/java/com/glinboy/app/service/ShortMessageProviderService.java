package com.glinboy.app.service;

import java.util.List;

import com.glinboy.app.service.dto.ShortMessageDTO;

public interface ShortMessageProviderService<T extends ShortMessageDTO> {

	void sendSMS(T t);
	void sendSMS(List<T> t);

}
