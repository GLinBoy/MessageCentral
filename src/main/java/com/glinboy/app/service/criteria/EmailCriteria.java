package com.glinboy.app.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
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
public class EmailCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter receiver;

    private StringFilter subject;

    public EmailCriteria() {}

    public EmailCriteria(EmailCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.receiver = other.receiver == null ? null : other.receiver.copy();
        this.subject = other.subject == null ? null : other.subject.copy();
    }

    @Override
    public EmailCriteria copy() {
        return new EmailCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getReceiver() {
        return receiver;
    }

    public StringFilter receiver() {
        if (receiver == null) {
            receiver = new StringFilter();
        }
        return receiver;
    }

    public void setReceiver(StringFilter receiver) {
        this.receiver = receiver;
    }

    public StringFilter getSubject() {
        return subject;
    }

    public StringFilter subject() {
        if (subject == null) {
            subject = new StringFilter();
        }
        return subject;
    }

    public void setSubject(StringFilter subject) {
        this.subject = subject;
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
        return Objects.equals(id, that.id) && Objects.equals(receiver, that.receiver) && Objects.equals(subject, that.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, receiver, subject);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmailCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (receiver != null ? "receiver=" + receiver + ", " : "") +
            (subject != null ? "subject=" + subject + ", " : "") +
            "}";
    }
}
