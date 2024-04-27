package com.glinboy.app.service.mapper;

import static com.glinboy.app.domain.EmailAsserts.assertEmailAllPropertiesEquals;
import static com.glinboy.app.domain.EmailTestSamples.getEmailSample1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmailMapperTest {

    private EmailMapper emailMapper;

    @BeforeEach
    void setUp() {
        emailMapper = new EmailMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEmailSample1();
        var actual = emailMapper.toEntity(emailMapper.toDto(expected));
        assertEmailAllPropertiesEquals(expected, actual);
    }
}
