package com.glinboy.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class NotificationDataTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static NotificationData getNotificationDataSample1() {
        return new NotificationData().id(1L).dataKey("dataKey1").dataValue("dataValue1");
    }

    public static NotificationData getNotificationDataSample2() {
        return new NotificationData().id(2L).dataKey("dataKey2").dataValue("dataValue2");
    }

    public static NotificationData getNotificationDataRandomSampleGenerator() {
        return new NotificationData()
            .id(longCount.incrementAndGet())
            .dataKey(UUID.randomUUID().toString())
            .dataValue(UUID.randomUUID().toString());
    }
}
