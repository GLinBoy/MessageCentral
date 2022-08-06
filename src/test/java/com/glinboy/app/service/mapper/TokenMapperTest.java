package com.glinboy.app.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenMapperTest {

    private TokenMapper tokenMapper;

    @BeforeEach
    public void setUp() {
        tokenMapper = new TokenMapperImpl();
    }
}
