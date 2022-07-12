package com.glinboy.app.domain;

import java.time.Instant;
import java.util.Objects;

public class MessagesStatics {
    public MessagesStatics(Instant date, Statics email, Statics sms, Statics notification) {
        this.date = date;
        this.email = email;
        this.sms = sms;
        this.notification = notification;
    }

    private Instant date;
    private Statics email;
    private Statics sms;
    private Statics notification;

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Statics getEmail() {
        return email;
    }

    public void setEmail(Statics email) {
        this.email = email;
    }

    public Statics getSms() {
        return sms;
    }

    public void setSms(Statics sms) {
        this.sms = sms;
    }

    public Statics getNotification() {
        return notification;
    }

    public void setNotification(Statics notification) {
        this.notification = notification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessagesStatics that = (MessagesStatics) o;
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
