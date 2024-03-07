package com.glinboy.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class NotificationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Notification getNotificationSample1() {
        return new Notification()
            .id(1L)
            .username("username1")
            .token("token1")
            .subject("subject1")
            .content("content1")
            .image("image1")
            .createdBy("createdBy1");
    }

    public static Notification getNotificationSample2() {
        return new Notification()
            .id(2L)
            .username("username2")
            .token("token2")
            .subject("subject2")
            .content("content2")
            .image("image2")
            .createdBy("createdBy2");
    }

    public static Notification getNotificationRandomSampleGenerator() {
        return new Notification()
            .id(longCount.incrementAndGet())
            .username(UUID.randomUUID().toString())
            .token(UUID.randomUUID().toString())
            .subject(UUID.randomUUID().toString())
            .content(UUID.randomUUID().toString())
            .image(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString());
    }
}
