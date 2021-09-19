package com.glinboy.app.domain;

import com.glinboy.app.domain.enumeration.MessageStatus;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ShortMessage.
 */
@Entity
@Table(name = "short_message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ShortMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 7, max = 15)
    @Pattern(regexp = "^\\+(?:[0-9]●?){6,14}[0-9]$")
    @Column(name = "phone_number", length = 15, nullable = false)
    private String phoneNumber;

    @NotNull
    @Size(min = 6, max = 160)
    @Column(name = "content", length = 160, nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MessageStatus status;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ShortMessage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public ShortMessage phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContent() {
        return this.content;
    }

    public ShortMessage content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageStatus getStatus() {
        return this.status;
    }

    public ShortMessage status(MessageStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShortMessage)) {
            return false;
        }
        return id != null && id.equals(((ShortMessage) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShortMessage{" +
            "id=" + getId() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", content='" + getContent() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
