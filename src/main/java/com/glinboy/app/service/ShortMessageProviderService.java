package com.glinboy.app.service;

import com.glinboy.app.service.dto.ShortMessageDTO;

public interface ShortMessageProviderService<T extends ShortMessageDTO> extends ChannelService<T> {
}
