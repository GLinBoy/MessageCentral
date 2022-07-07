package com.glinboy.app.domain;

import com.glinboy.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationDataTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationData.class);
        NotificationData notificationData1 = new NotificationData();
        notificationData1.setId(1L);
        NotificationData notificationData2 = new NotificationData();
        notificationData2.setId(notificationData1.getId());
        assertThat(notificationData1).isEqualTo(notificationData2);
        notificationData2.setId(2L);
        assertThat(notificationData1).isNotEqualTo(notificationData2);
        notificationData1.setId(null);
        assertThat(notificationData1).isNotEqualTo(notificationData2);
    }
}
