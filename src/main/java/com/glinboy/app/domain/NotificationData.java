package com.glinboy.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A NotificationData.
 */
@Entity
@Table(name = "notification_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 128)
    @Column(name = "data_key", length = 128, nullable = false)
    private String dataKey;

    @NotNull
    @Size(max = 256)
    @Column(name = "data_value", length = 256, nullable = false)
    private String dataValue;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "data" }, allowSetters = true)
    private Notification notification;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public NotificationData id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataKey() {
        return this.dataKey;
    }

    public NotificationData dataKey(String dataKey) {
        this.setDataKey(dataKey);
        return this;
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }

    public String getDataValue() {
        return this.dataValue;
    }

    public NotificationData dataValue(String dataValue) {
        this.setDataValue(dataValue);
        return this;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public Notification getNotification() {
        return this.notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public NotificationData notification(Notification notification) {
        this.setNotification(notification);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationData)) {
            return false;
        }
        return id != null && id.equals(((NotificationData) o).id);
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
            ", dataKey='" + getDataKey() + "'" +
            ", dataValue='" + getDataValue() + "'" +
            "}";
    }
}
