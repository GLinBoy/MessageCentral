package com.glinboy.app.service.mapper;

import static com.glinboy.app.domain.NotificationAsserts.assertNotificationAllPropertiesEquals;
import static com.glinboy.app.domain.NotificationTestSamples.getNotificationSample1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationMapperTest {

    private NotificationMapper notificationMapper;

    @BeforeEach
    void setUp() {
        notificationMapper = new NotificationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getNotificationSample1();
        var actual = notificationMapper.toEntity(notificationMapper.toDto(expected));
        assertNotificationAllPropertiesEquals(expected, actual);
    }
}
