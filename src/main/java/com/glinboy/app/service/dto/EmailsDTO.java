package com.glinboy.app.service.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Lob;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.glinboy.app.util.Patterns;

/**
 * A DTO for the {@link com.glinboy.app.domain.Email} entity.
 */
public class EmailsDTO implements Serializable {

    @NotEmpty
    private Set<String> receivers;

    @NotNull
    @Size(min = 4, max = 128)
    private String subject;

    @Lob
    @NotNull
    private String content;

    public Set<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(Set<String> receivers) {
        this.receivers = receivers;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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
        if (!(o instanceof EmailsDTO)) {
            return false;
        }

        EmailsDTO emailsDTO = (EmailsDTO) o;
        return Objects.equals(this.receivers.size(), emailsDTO.receivers.size()) &&
                this.receivers.containsAll(emailsDTO.receivers) &&
                Objects.equals(this.subject, emailsDTO.subject) &&
                Objects.equals(this.content, emailsDTO.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.subject + this.content);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmailDTO{" +
            ", receivers='" + getReceivers().stream().collect(Collectors.joining(", ")) + "'" +
            ", subject='" + getSubject() + "'" +
            ", content='" + getContent() + "'" +
            "}";
    }
}
