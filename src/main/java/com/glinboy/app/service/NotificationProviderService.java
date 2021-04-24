package com.glinboy.app.service;

import com.glinboy.app.service.dto.NotificationDTO;

public interface NotificationProviderService<T extends NotificationDTO> {

	void sendNotification(T t);

}
