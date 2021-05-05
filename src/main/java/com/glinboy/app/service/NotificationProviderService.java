package com.glinboy.app.service;

import java.util.List;

import com.glinboy.app.service.dto.NotificationDTO;

public interface NotificationProviderService<T extends NotificationDTO> {

	void sendNotification(T t);

	void sendNotification(List<NotificationDTO> dtoList);

}
