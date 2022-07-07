package com.glinboy.app.service.dto;

import com.glinboy.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ShortMessageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShortMessageDTO.class);
        ShortMessageDTO shortMessageDTO1 = new ShortMessageDTO();
        shortMessageDTO1.setId(1L);
        ShortMessageDTO shortMessageDTO2 = new ShortMessageDTO();
        assertThat(shortMessageDTO1).isNotEqualTo(shortMessageDTO2);
        shortMessageDTO2.setId(shortMessageDTO1.getId());
        assertThat(shortMessageDTO1).isEqualTo(shortMessageDTO2);
        shortMessageDTO2.setId(2L);
        assertThat(shortMessageDTO1).isNotEqualTo(shortMessageDTO2);
        shortMessageDTO1.setId(null);
        assertThat(shortMessageDTO1).isNotEqualTo(shortMessageDTO2);
    }
}
