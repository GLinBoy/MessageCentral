package com.glinboy.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ShortMessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ShortMessage getShortMessageSample1() {
        return new ShortMessage().id(1L).phoneNumber("phoneNumber1").content("content1").createdBy("createdBy1");
    }

    public static ShortMessage getShortMessageSample2() {
        return new ShortMessage().id(2L).phoneNumber("phoneNumber2").content("content2").createdBy("createdBy2");
    }

    public static ShortMessage getShortMessageRandomSampleGenerator() {
        return new ShortMessage()
            .id(longCount.incrementAndGet())
            .phoneNumber(UUID.randomUUID().toString())
            .content(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString());
    }
}
