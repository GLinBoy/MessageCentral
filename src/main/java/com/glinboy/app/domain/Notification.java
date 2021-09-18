package com.glinboy.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.glinboy.app.domain.enumeration.MessageStatus;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Notification.
 */
@Entity
@Table(name = "notification")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 64)
    @Column(name = "username", length = 64, nullable = false)
    private String username;

    @NotNull
    @Size(max = 164)
    @Column(name = "token", length = 164, nullable = false)
    private String token;

    @NotNull
    @Size(max = 128)
    @Column(name = "subject", length = 128, nullable = false)
    private String subject;

    @NotNull
    @Size(max = 4000)
    @Column(name = "content", length = 4000, nullable = false)
    private String content;

    @Size(max = 256)
    @Column(name = "image", length = 256)
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MessageStatus status;

    @OneToMany(mappedBy = "notification")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "notification" }, allowSetters = true)
    private Set<NotificationData> data = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Notification id(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return this.username;
    }

    public Notification username(String username) {
        this.username = username;
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return this.token;
    }

    public Notification token(String token) {
        this.token = token;
        return this;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSubject() {
        return this.subject;
    }

    public Notification subject(String subject) {
        this.subject = subject;
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return this.content;
    }

    public Notification content(String content) {
        this.content = content;
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return this.image;
    }

    public Notification image(String image) {
        this.image = image;
        return this;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public MessageStatus getStatus() {
        return this.status;
    }

    public Notification status(MessageStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Set<NotificationData> getData() {
        return this.data;
    }

    public Notification data(Set<NotificationData> notificationData) {
        this.setData(notificationData);
        return this;
    }

    public Notification addData(NotificationData notificationData) {
        this.data.add(notificationData);
        notificationData.setNotification(this);
        return this;
    }

    public Notification removeData(NotificationData notificationData) {
        this.data.remove(notificationData);
        notificationData.setNotification(null);
        return this;
    }

    public void setData(Set<NotificationData> notificationData) {
        if (this.data != null) {
            this.data.forEach(i -> i.setNotification(null));
        }
        if (notificationData != null) {
            notificationData.forEach(i -> i.setNotification(this));
        }
        this.data = notificationData;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification)) {
            return false;
        }
        return id != null && id.equals(((Notification) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Notification{" +
            "id=" + getId() +
            ", username='" + getUsername() + "'" +
            ", token='" + getToken() + "'" +
            ", subject='" + getSubject() + "'" +
            ", content='" + getContent() + "'" +
            ", image='" + getImage() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
