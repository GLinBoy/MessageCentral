package com.glinboy.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.glinboy.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Email.class);
        Email email1 = new Email();
        email1.setId(1L);
        Email email2 = new Email();
        email2.setId(email1.getId());
        assertThat(email1).isEqualTo(email2);
        email2.setId(2L);
        assertThat(email1).isNotEqualTo(email2);
        email1.setId(null);
        assertThat(email1).isNotEqualTo(email2);
    }
}
