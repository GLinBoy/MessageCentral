package com.glinboy.app.domain;

import static com.glinboy.app.domain.TokenTestSamples.getTokenSample1;
import static com.glinboy.app.domain.TokenTestSamples.getTokenSample2;
import static org.assertj.core.api.Assertions.assertThat;

import com.glinboy.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TokenTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Token.class);
        Token token1 = getTokenSample1();
        Token token2 = new Token();
        assertThat(token1).isNotEqualTo(token2);

        token2.setId(token1.getId());
        assertThat(token1).isEqualTo(token2);

        token2 = getTokenSample2();
        assertThat(token1).isNotEqualTo(token2);
    }
}
