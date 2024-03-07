package com.glinboy.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EmailTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Email getEmailSample1() {
        return new Email().id(1L).receiver("receiver1").subject("subject1").createdBy("createdBy1");
    }

    public static Email getEmailSample2() {
        return new Email().id(2L).receiver("receiver2").subject("subject2").createdBy("createdBy2");
    }

    public static Email getEmailRandomSampleGenerator() {
        return new Email()
            .id(longCount.incrementAndGet())
            .receiver(UUID.randomUUID().toString())
            .subject(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString());
    }
}
