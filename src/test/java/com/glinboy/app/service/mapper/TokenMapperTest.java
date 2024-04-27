package com.glinboy.app.service.mapper;

import static com.glinboy.app.domain.TokenAsserts.assertTokenAllPropertiesEquals;
import static com.glinboy.app.domain.TokenTestSamples.getTokenSample1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenMapperTest {

    private TokenMapper tokenMapper;

    @BeforeEach
    void setUp() {
        tokenMapper = new TokenMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTokenSample1();
        var actual = tokenMapper.toEntity(tokenMapper.toDto(expected));
        assertTokenAllPropertiesEquals(expected, actual);
    }
}
