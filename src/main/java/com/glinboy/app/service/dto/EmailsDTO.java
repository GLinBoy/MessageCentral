package com.glinboy.app.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Lob;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.collections.CollectionUtils;

/**
 * A DTO for the {@link com.glinboy.app.domain.Email} entity.
 */
public class EmailsDTO implements Serializable {

    @NotEmpty
    private Set<String> receivers = new HashSet<>();

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
        return !CollectionUtils.isEmpty(this.receivers) &&
                this.receivers.size() == emailsDTO.receivers.size() &&
                this.receivers.containsAll(emailsDTO.receivers) &&
                (this.subject == null ? emailsDTO.subject == null : this.subject.equals(emailsDTO.subject)) &&
                (this.content == null ? emailsDTO.content == null : this.content.equals(emailsDTO.content));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getReceivers().stream().collect(Collectors.joining(", ")) + this.subject + this.content);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmailDTO{" +
            "receivers='" + getReceivers().stream().collect(Collectors.joining(", ")) + "'" +
            ", subject='" + getSubject() + "'" +
            ", content='" + getContent() + "'" +
            "}";
    }
}
