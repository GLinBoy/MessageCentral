package com.glinboy.app.service;

import com.glinboy.app.service.dto.EmailDTO;

public interface MailProviderService<T extends EmailDTO> {

	void sendEmail(T t);

}
