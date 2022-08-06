package com.glinboy.app.service.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A NotificationDataDTO.
 */
public class NotificationDataDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 128)
    private String dataKey;

    @NotNull
    @Size(max = 256)
    private String dataValue;

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

    public String getDataKey() {
        return this.dataKey;
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }

    public NotificationDataDTO key(String key) {
        this.dataKey = key;
        return this;
    }

    public String getDataValue() {
        return this.dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public NotificationDataDTO value(String value) {
        this.dataValue = value;
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
            ", key='" + getDataKey() + "'" +
            ", value='" + getDataValue() + "'" +
            ", notificationId='" + getNotificationId() + "'" +
            "}";
    }
}
