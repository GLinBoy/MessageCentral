package com.glinboy.app.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class TokenMapperTest {

    private TokenMapper tokenMapper;

    @BeforeEach
    public void setUp() {
        tokenMapper = new TokenMapperImpl();
    }
}
