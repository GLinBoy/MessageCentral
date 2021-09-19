package com.glinboy.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.glinboy.app.web.rest.TestUtil;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ShortMessagesDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShortMessagesDTO.class);
        Set<String> numbers = Sets.newHashSet("+989129882954", "+989129892874");
        ShortMessagesDTO shortMessagesDTO1 = new ShortMessagesDTO();
        shortMessagesDTO1.setPhoneNumber(numbers);
        ShortMessagesDTO shortMessagesDTO2 = new ShortMessagesDTO();
        assertThat(shortMessagesDTO1).isNotEqualTo(shortMessagesDTO2);
        shortMessagesDTO2.setPhoneNumber(shortMessagesDTO1.getPhoneNumbers());
        assertThat(shortMessagesDTO1).isEqualTo(shortMessagesDTO2);
        Set<String> numbers2 = Sets.newHashSet(numbers);
        numbers.add("+989129977335");
        shortMessagesDTO2.setPhoneNumber(numbers2);
        assertThat(shortMessagesDTO1).isNotEqualTo(shortMessagesDTO2);
        shortMessagesDTO1.setPhoneNumber(new HashSet<>());
        assertThat(shortMessagesDTO1).isNotEqualTo(shortMessagesDTO2);
    }
}
