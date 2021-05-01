package com.glinboy.app.service;

import java.util.List;

import com.glinboy.app.service.dto.EmailDTO;

public interface MailProviderService<T extends EmailDTO> {

	void sendEmail(T t);

	void sendEmail(List<T> dtoList);

}
