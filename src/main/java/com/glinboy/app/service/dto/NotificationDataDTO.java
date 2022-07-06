package com.glinboy.app.service.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A NotificationDataDTO.
 */
public class NotificationDataDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 128)
    private String key;

    @NotNull
    @Size(max = 256)
    private String value;

    private Long notificationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationDataDTO id(Long id) {
        this.id = id;
        return this;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public NotificationDataDTO key(String key) {
        this.key = key;
        return this;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public NotificationDataDTO value(String value) {
        this.value = value;
        return this;
    }

    public Long getNotificationId() {
        return this.notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationDataDTO)) {
            return false;
        }
        return id != null && id.equals(((NotificationDataDTO) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationData{" +
            "id=" + getId() +
            ", key='" + getKey() + "'" +
            ", value='" + getValue() + "'" +
            ", notificationId='" + getNotificationId() + "'" +
            "}";
    }
}
