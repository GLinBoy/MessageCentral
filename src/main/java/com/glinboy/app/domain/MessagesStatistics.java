package com.glinboy.app.domain;

import java.time.Instant;
import java.util.Objects;

public class MessagesStatistics {
    public MessagesStatistics(Instant date, Statistics email, Statistics sms, Statistics notification) {
        this.date = date;
        this.email = email;
        this.sms = sms;
        this.notification = notification;
    }

    private Instant date;
    private Statistics email;
    private Statistics sms;
    private Statistics notification;

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Statistics getEmail() {
        return email;
    }

    public void setEmail(Statistics email) {
        this.email = email;
    }

    public Statistics getSms() {
        return sms;
    }

    public void setSms(Statistics sms) {
        this.sms = sms;
    }

    public Statistics getNotification() {
        return notification;
    }

    public void setNotification(Statistics notification) {
        this.notification = notification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessagesStatistics that = (MessagesStatistics) o;
        return date.equals(that.date) && email.equals(that.email) && sms.equals(that.sms) && notification.equals(that.notification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, email, sms, notification);
    }

    @Override
    public String toString() {
        return "MessagesStatic{" +
            "date=" + date +
            ", email=" + email +
            ", sms=" + sms +
            ", notification=" + notification +
            '}';
    }
}
