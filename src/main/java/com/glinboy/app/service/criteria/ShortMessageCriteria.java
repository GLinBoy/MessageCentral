package com.glinboy.app.service.criteria;

import com.glinboy.app.domain.enumeration.MessageStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.glinboy.app.domain.ShortMessage} entity. This class is used
 * in {@link com.glinboy.app.web.rest.ShortMessageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /short-messages?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShortMessageCriteria implements Serializable, Criteria {

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

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter phoneNumber;

    private StringFilter content;

    private MessageStatusFilter status;

    private InstantFilter createdAt;

    private StringFilter createdBy;

    private Boolean distinct;

    public ShortMessageCriteria() {}

    public ShortMessageCriteria(ShortMessageCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.phoneNumber = other.phoneNumber == null ? null : other.phoneNumber.copy();
        this.content = other.content == null ? null : other.content.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ShortMessageCriteria copy() {
        return new ShortMessageCriteria(this);
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

    public StringFilter getPhoneNumber() {
        return phoneNumber;
    }

    public StringFilter phoneNumber() {
        if (phoneNumber == null) {
            phoneNumber = new StringFilter();
        }
        return phoneNumber;
    }

    public void setPhoneNumber(StringFilter phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public StringFilter getContent() {
        return content;
    }

    public StringFilter content() {
        if (content == null) {
            content = new StringFilter();
        }
        return content;
    }

    public void setContent(StringFilter content) {
        this.content = content;
    }

    public MessageStatusFilter getStatus() {
        return status;
    }

    public MessageStatusFilter status() {
        if (status == null) {
            status = new MessageStatusFilter();
        }
        return status;
    }

    public void setStatus(MessageStatusFilter status) {
        this.status = status;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            createdAt = new InstantFilter();
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            createdBy = new StringFilter();
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public Boolean getDistinct() {
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
        final ShortMessageCriteria that = (ShortMessageCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(content, that.content) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phoneNumber, content, status, createdAt, createdBy, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShortMessageCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (phoneNumber != null ? "phoneNumber=" + phoneNumber + ", " : "") +
            (content != null ? "content=" + content + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
