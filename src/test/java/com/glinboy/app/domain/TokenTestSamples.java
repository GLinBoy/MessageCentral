package com.glinboy.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TokenTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Token getTokenSample1() {
        return new Token().id(1L).name("name1").token("token1").roles(1).createdBy("createdBy1").updatedBy("updatedBy1");
    }

    public static Token getTokenSample2() {
        return new Token().id(2L).name("name2").token("token2").roles(2).createdBy("createdBy2").updatedBy("updatedBy2");
    }

    public static Token getTokenRandomSampleGenerator() {
        return new Token()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .token(UUID.randomUUID().toString())
            .roles(intCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .updatedBy(UUID.randomUUID().toString());
    }
}
