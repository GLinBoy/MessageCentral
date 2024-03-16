package com.glinboy.app.domain;

import static com.glinboy.app.domain.ShortMessageTestSamples.getShortMessageSample1;
import static com.glinboy.app.domain.ShortMessageTestSamples.getShortMessageSample2;
import static org.assertj.core.api.Assertions.assertThat;

import com.glinboy.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShortMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShortMessage.class);
        ShortMessage shortMessage1 = getShortMessageSample1();
        ShortMessage shortMessage2 = new ShortMessage();
        assertThat(shortMessage1).isNotEqualTo(shortMessage2);

        shortMessage2.setId(shortMessage1.getId());
        assertThat(shortMessage1).isEqualTo(shortMessage2);

        shortMessage2 = getShortMessageSample2();
        assertThat(shortMessage1).isNotEqualTo(shortMessage2);
    }
}
