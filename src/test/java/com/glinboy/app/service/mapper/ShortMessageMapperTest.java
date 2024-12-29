package com.glinboy.app.service.mapper;

import static com.glinboy.app.domain.ShortMessageAsserts.*;
import static com.glinboy.app.domain.ShortMessageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShortMessageMapperTest {

    private ShortMessageMapper shortMessageMapper;

    @BeforeEach
    void setUp() {
        shortMessageMapper = new ShortMessageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getShortMessageSample1();
        var actual = shortMessageMapper.toEntity(shortMessageMapper.toDto(expected));
        assertShortMessageAllPropertiesEquals(expected, actual);
    }
}
