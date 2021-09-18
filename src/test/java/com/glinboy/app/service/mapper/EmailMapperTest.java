package com.glinboy.app.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmailMapperTest {

    private EmailMapper emailMapper;

    @BeforeEach
    public void setUp() {
        emailMapper = new EmailMapperImpl();
    }
}
