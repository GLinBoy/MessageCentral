package com.glinboy.app.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShortMessageMapperTest {

    private ShortMessageMapper shortMessageMapper;

    @BeforeEach
    public void setUp() {
        shortMessageMapper = new ShortMessageMapperImpl();
    }
}
