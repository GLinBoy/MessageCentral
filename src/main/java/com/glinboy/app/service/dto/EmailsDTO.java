package com.glinboy.app.service.dto;

import com.glinboy.app.domain.enumeration.EmailType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    private EmailType emailType = EmailType.HTML;

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

    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
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
        return (
            this.receivers != null &&
            !this.receivers.isEmpty() &&
            this.receivers.size() == emailsDTO.receivers.size() &&
            this.receivers.containsAll(emailsDTO.receivers) &&
            (this.subject == null ? emailsDTO.subject == null : this.subject.equals(emailsDTO.subject)) &&
            (this.content == null ? emailsDTO.content == null : this.content.equals(emailsDTO.content)) &&
            this.emailType.equals(emailsDTO.emailType)
        );
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
            ", emailType='" + getEmailType() + "'" +
            "}";
    }
}
