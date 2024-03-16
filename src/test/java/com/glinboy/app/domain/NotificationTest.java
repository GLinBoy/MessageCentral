package com.glinboy.app.domain;

import static com.glinboy.app.domain.NotificationDataTestSamples.getNotificationDataRandomSampleGenerator;
import static com.glinboy.app.domain.NotificationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.glinboy.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class NotificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notification.class);
        Notification notification1 = getNotificationSample1();
        Notification notification2 = new Notification();
        assertThat(notification1).isNotEqualTo(notification2);

        notification2.setId(notification1.getId());
        assertThat(notification1).isEqualTo(notification2);

        notification2 = getNotificationSample2();
        assertThat(notification1).isNotEqualTo(notification2);
    }

    @Test
    void dataTest() throws Exception {
        Notification notification = getNotificationRandomSampleGenerator();
        NotificationData notificationDataBack = getNotificationDataRandomSampleGenerator();

        notification.addData(notificationDataBack);
        assertThat(notification.getData()).containsOnly(notificationDataBack);
        assertThat(notificationDataBack.getNotification()).isEqualTo(notification);

        notification.removeData(notificationDataBack);
        assertThat(notification.getData()).doesNotContain(notificationDataBack);
        assertThat(notificationDataBack.getNotification()).isNull();

        notification.data(new HashSet<>(Set.of(notificationDataBack)));
        assertThat(notification.getData()).containsOnly(notificationDataBack);
        assertThat(notificationDataBack.getNotification()).isEqualTo(notification);

        notification.setData(new HashSet<>());
        assertThat(notification.getData()).doesNotContain(notificationDataBack);
        assertThat(notificationDataBack.getNotification()).isNull();
    }
}
