package com.glinboy.app.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A DTO for the {@link com.glinboy.app.domain.ShortMessage} entity.
 */
public class ShortMessagesDTO implements Serializable {

    @NotEmpty
    private Set<String> phoneNumbers = new HashSet<>();

    @NotNull
    @Size(min = 6, max = 160)
    private String content;

    public Set<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumber(Set<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
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
        if (!(o instanceof ShortMessagesDTO)) {
            return false;
        }

        ShortMessagesDTO shortMessagesDTO = (ShortMessagesDTO) o;

        return this.phoneNumbers != null && !this.phoneNumbers.isEmpty() &&
                this.phoneNumbers.size() == shortMessagesDTO.getPhoneNumbers().size() &&
                this.phoneNumbers.containsAll(shortMessagesDTO.phoneNumbers) &&
                (this.content == null ? shortMessagesDTO.content == null : this.content.equals(shortMessagesDTO.content));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.phoneNumbers.stream().collect(Collectors.joining(", ")) + this.content);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShortMessageDTO{" +
            ", phoneNumber='" + getPhoneNumbers().stream().collect(Collectors.joining(", ")) + "'" +
            ", content='" + getContent() + "'" +
            "}";
    }
}
