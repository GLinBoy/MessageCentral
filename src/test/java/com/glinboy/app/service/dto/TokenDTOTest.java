package com.glinboy.app.service.dto;

import com.glinboy.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TokenDTO.class);
        TokenDTO tokenDTO1 = new TokenDTO();
        tokenDTO1.setId(1L);
        TokenDTO tokenDTO2 = new TokenDTO();
        assertThat(tokenDTO1).isNotEqualTo(tokenDTO2);
        tokenDTO2.setId(tokenDTO1.getId());
        assertThat(tokenDTO1).isEqualTo(tokenDTO2);
        tokenDTO2.setId(2L);
        assertThat(tokenDTO1).isNotEqualTo(tokenDTO2);
        tokenDTO1.setId(null);
        assertThat(tokenDTO1).isNotEqualTo(tokenDTO2);
    }
}
