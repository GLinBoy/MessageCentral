package com.glinboy.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.glinboy.app.web.rest.TestUtil;
import com.google.common.collect.Sets;

class EmailsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        Set<String> reciver = Sets.newHashSet("test1@local", "test2@local");
        TestUtil.equalsVerifier(EmailsDTO.class);
        EmailsDTO emailsDTO1 = new EmailsDTO();
        emailsDTO1.setReceivers(reciver);;
        EmailsDTO emailsDTO2 = new EmailsDTO();
        assertThat(emailsDTO1).isNotEqualTo(emailsDTO2);
        emailsDTO2.setReceivers(emailsDTO1.getReceivers());
        assertThat(emailsDTO1).isEqualTo(emailsDTO2);
        Set<String> reciver2 = Sets.newHashSet(reciver);
        reciver2.add("test3@local");
        emailsDTO2.setReceivers(reciver2);
        assertThat(emailsDTO1).isNotEqualTo(emailsDTO2);
        emailsDTO1.setReceivers(null);
        assertThat(emailsDTO1).isNotEqualTo(emailsDTO2);
    }
}
