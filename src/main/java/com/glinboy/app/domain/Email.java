package com.glinboy.app.domain;

import com.glinboy.app.domain.enumeration.MessageStatus;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * A Email.
 */
@Entity
@Table(name = "email")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Email implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 8, max = 128)
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
    @Column(name = "receiver", length = 128, nullable = false)
    private String receiver;

    @NotNull
    @Size(min = 4, max = 128)
    @Column(name = "subject", length = 128, nullable = false)
    private String subject;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MessageStatus status;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Email id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiver() {
        return this.receiver;
    }

    public Email receiver(String receiver) {
        this.setReceiver(receiver);
        return this;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSubject() {
        return this.subject;
    }

    public Email subject(String subject) {
        this.setSubject(subject);
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return this.content;
    }

    public Email content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageStatus getStatus() {
        return this.status;
    }

    public Email status(MessageStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Email createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Email createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Email)) {
            return false;
        }
        return id != null && id.equals(((Email) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Email{" +
            "id=" + getId() +
            ", receiver='" + getReceiver() + "'" +
            ", subject='" + getSubject() + "'" +
            ", content='" + getContent() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            "}";
    }
}
