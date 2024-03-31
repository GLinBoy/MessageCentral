package com.glinboy.app.domain;

import static com.glinboy.app.domain.NotificationDataTestSamples.*;
import static com.glinboy.app.domain.NotificationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.glinboy.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotificationDataTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationData.class);
        NotificationData notificationData1 = getNotificationDataSample1();
        NotificationData notificationData2 = new NotificationData();
        assertThat(notificationData1).isNotEqualTo(notificationData2);

        notificationData2.setId(notificationData1.getId());
        assertThat(notificationData1).isEqualTo(notificationData2);

        notificationData2 = getNotificationDataSample2();
        assertThat(notificationData1).isNotEqualTo(notificationData2);
    }

    @Test
    void notificationTest() throws Exception {
        NotificationData notificationData = getNotificationDataRandomSampleGenerator();
        Notification notificationBack = getNotificationRandomSampleGenerator();

        notificationData.setNotification(notificationBack);
        assertThat(notificationData.getNotification()).isEqualTo(notificationBack);

        notificationData.notification(null);
        assertThat(notificationData.getNotification()).isNull();
    }
}
