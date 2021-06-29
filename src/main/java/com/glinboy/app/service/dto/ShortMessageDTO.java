package com.glinboy.app.service.dto;

import java.io.Serializable;
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
            "}";
    }
}
