package com.glinboy.app.service.criteria;

import com.glinboy.app.domain.enumeration.EmailType;
import com.glinboy.app.domain.enumeration.MessageStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.glinboy.app.domain.Email} entity. This class is used
 * in {@link com.glinboy.app.web.rest.EmailResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /emails?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EmailCriteria implements Serializable, Criteria {

    /**
     * Class for filtering MessageStatus
     */
    public static class MessageStatusFilter extends Filter<MessageStatus> {

        public MessageStatusFilter() {}

        public MessageStatusFilter(MessageStatusFilter filter) {
            super(filter);
        }

        @Override
        public MessageStatusFilter copy() {
            return new MessageStatusFilter(this);
        }
    }

    /**
     * Class for filtering EmailType
     */
    public static class EmailTypeFilter extends Filter<EmailType> {

        public EmailTypeFilter() {}

        public EmailTypeFilter(EmailTypeFilter filter) {
            super(filter);
        }

        @Override
        public EmailTypeFilter copy() {
            return new EmailTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter receiver;

    private StringFilter subject;

    private MessageStatusFilter status;

    private EmailTypeFilter emailType;

    private InstantFilter createdAt;

    private StringFilter createdBy;

    private Boolean distinct;

    public EmailCriteria() {}

    public EmailCriteria(EmailCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.receiver = other.optionalReceiver().map(StringFilter::copy).orElse(null);
        this.subject = other.optionalSubject().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(MessageStatusFilter::copy).orElse(null);
        this.emailType = other.optionalEmailType().map(EmailTypeFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public EmailCriteria copy() {
        return new EmailCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getReceiver() {
        return receiver;
    }

    public Optional<StringFilter> optionalReceiver() {
        return Optional.ofNullable(receiver);
    }

    public StringFilter receiver() {
        if (receiver == null) {
            setReceiver(new StringFilter());
        }
        return receiver;
    }

    public void setReceiver(StringFilter receiver) {
        this.receiver = receiver;
    }

    public StringFilter getSubject() {
        return subject;
    }

    public Optional<StringFilter> optionalSubject() {
        return Optional.ofNullable(subject);
    }

    public StringFilter subject() {
        if (subject == null) {
            setSubject(new StringFilter());
        }
        return subject;
    }

    public void setSubject(StringFilter subject) {
        this.subject = subject;
    }

    public MessageStatusFilter getStatus() {
        return status;
    }

    public Optional<MessageStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public MessageStatusFilter status() {
        if (status == null) {
            setStatus(new MessageStatusFilter());
        }
        return status;
    }

    public void setStatus(MessageStatusFilter status) {
        this.status = status;
    }

    public EmailTypeFilter getEmailType() {
        return emailType;
    }

    public Optional<EmailTypeFilter> optionalEmailType() {
        return Optional.ofNullable(emailType);
    }

    public EmailTypeFilter emailType() {
        if (emailType == null) {
            setEmailType(new EmailTypeFilter());
        }
        return emailType;
    }

    public void setEmailType(EmailTypeFilter emailType) {
        this.emailType = emailType;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public Optional<StringFilter> optionalCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            setCreatedBy(new StringFilter());
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EmailCriteria that = (EmailCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(receiver, that.receiver) &&
            Objects.equals(subject, that.subject) &&
            Objects.equals(status, that.status) &&
            Objects.equals(emailType, that.emailType) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, receiver, subject, status, emailType, createdAt, createdBy, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmailCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReceiver().map(f -> "receiver=" + f + ", ").orElse("") +
            optionalSubject().map(f -> "subject=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalEmailType().map(f -> "emailType=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
