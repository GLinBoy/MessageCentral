package com.glinboy.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.glinboy.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TokenTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Token.class);
        Token token1 = new Token();
        token1.setId(1L);
        Token token2 = new Token();
        token2.setId(token1.getId());
        assertThat(token1).isEqualTo(token2);
        token2.setId(2L);
        assertThat(token1).isNotEqualTo(token2);
        token1.setId(null);
        assertThat(token1).isNotEqualTo(token2);
    }
}
