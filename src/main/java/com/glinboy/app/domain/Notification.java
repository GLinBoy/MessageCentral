package com.glinboy.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.glinboy.app.domain.enumeration.MessageStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * A Notification.
 */
@Entity
@Table(name = "notification")
@EntityListeners(AuditingEntityListener.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
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

    @NotNull
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @CreatedBy
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "notification" }, allowSetters = true)
    private Set<NotificationData> data = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Notification id(Long id) {
        this.setId(id);
        return this;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Notification username(String username) {
        this.setUsername(username);
        return this;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Notification token(String token) {
        this.setToken(token);
        return this;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Notification subject(String subject) {
        this.setSubject(subject);
        return this;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Notification content(String content) {
        this.setContent(content);
        return this;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Notification image(String image) {
        this.setImage(image);
        return this;
    }

    public MessageStatus getStatus() {
        return this.status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Notification status(MessageStatus status) {
        this.setStatus(status);
        return this;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Notification createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Notification createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public Set<NotificationData> getData() {
        return this.data;
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
            ", createdAt='" + getCreatedAt() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            "}";
    }
}
