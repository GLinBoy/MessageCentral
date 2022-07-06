package com.glinboy.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A NotificationData.
 */
@Entity
@Table(name = "notification_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NotificationData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 128)
    @Column(name = "key", length = 128, nullable = false)
    private String key;

    @NotNull
    @Size(max = 256)
    @Column(name = "value", length = 256, nullable = false)
    private String value;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @NotNull
    @JsonIgnoreProperties(value = {"data"}, allowSetters = true)
    private Notification notification;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationData id(Long id) {
        this.setId(id);
        return this;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public NotificationData key(String key) {
        this.setKey(key);
        return this;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public NotificationData value(String value) {
        this.setValue(value);
        return this;
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
            ", key='" + getKey() + "'" +
            ", value='" + getValue() + "'" +
            "}";
    }
}
