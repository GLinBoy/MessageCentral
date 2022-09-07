package com.glinboy.app.service.dto;

import com.glinboy.app.domain.enumeration.MessageStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.glinboy.app.domain.ShortMessage} entity.
 */
public class ShortMessageDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 7, max = 15)
    @Pattern(regexp = "^\\+(?:[0-9]●?){6,14}[0-9]$")
    private String phoneNumber;

    @NotNull
    @Size(min = 6, max = 160)
    private String content;

    private MessageStatus status;

    @NotNull
    private Instant createdAt;

    @NotNull
    private String createdBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShortMessageDTO)) {
            return false;
        }

        ShortMessageDTO shortMessageDTO = (ShortMessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, shortMessageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShortMessageDTO{" +
            "id=" + getId() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", content='" + getContent() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            "}";
    }
}
