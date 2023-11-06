package com.glinboy.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.glinboy.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShortMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShortMessage.class);
        ShortMessage shortMessage1 = new ShortMessage();
        shortMessage1.setId(1L);
        ShortMessage shortMessage2 = new ShortMessage();
        shortMessage2.setId(shortMessage1.getId());
        assertThat(shortMessage1).isEqualTo(shortMessage2);
        shortMessage2.setId(2L);
        assertThat(shortMessage1).isNotEqualTo(shortMessage2);
        shortMessage1.setId(null);
        assertThat(shortMessage1).isNotEqualTo(shortMessage2);
    }
}
