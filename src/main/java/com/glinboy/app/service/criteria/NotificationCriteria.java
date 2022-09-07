package com.glinboy.app.service.criteria;

import com.glinboy.app.domain.enumeration.MessageStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.glinboy.app.domain.Notification} entity. This class is used
 * in {@link com.glinboy.app.web.rest.NotificationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /notifications?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationCriteria implements Serializable, Criteria {

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

    private StringFilter username;

    private StringFilter token;

    private StringFilter subject;

    private StringFilter content;

    private StringFilter image;

    private MessageStatusFilter status;

    private InstantFilter createdAt;

    private StringFilter createdBy;

    private LongFilter dataId;

    private Boolean distinct;

    public NotificationCriteria() {}

    public NotificationCriteria(NotificationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.username = other.username == null ? null : other.username.copy();
        this.token = other.token == null ? null : other.token.copy();
        this.subject = other.subject == null ? null : other.subject.copy();
        this.content = other.content == null ? null : other.content.copy();
        this.image = other.image == null ? null : other.image.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.dataId = other.dataId == null ? null : other.dataId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public NotificationCriteria copy() {
        return new NotificationCriteria(this);
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

    public StringFilter getUsername() {
        return username;
    }

    public StringFilter username() {
        if (username == null) {
            username = new StringFilter();
        }
        return username;
    }

    public void setUsername(StringFilter username) {
        this.username = username;
    }

    public StringFilter getToken() {
        return token;
    }

    public StringFilter token() {
        if (token == null) {
            token = new StringFilter();
        }
        return token;
    }

    public void setToken(StringFilter token) {
        this.token = token;
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

    public StringFilter getImage() {
        return image;
    }

    public StringFilter image() {
        if (image == null) {
            image = new StringFilter();
        }
        return image;
    }

    public void setImage(StringFilter image) {
        this.image = image;
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

    public LongFilter getDataId() {
        return dataId;
    }

    public LongFilter dataId() {
        if (dataId == null) {
            dataId = new LongFilter();
        }
        return dataId;
    }

    public void setDataId(LongFilter dataId) {
        this.dataId = dataId;
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
        final NotificationCriteria that = (NotificationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(username, that.username) &&
            Objects.equals(token, that.token) &&
            Objects.equals(subject, that.subject) &&
            Objects.equals(content, that.content) &&
            Objects.equals(image, that.image) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(dataId, that.dataId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, token, subject, content, image, status, createdAt, createdBy, dataId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (username != null ? "username=" + username + ", " : "") +
            (token != null ? "token=" + token + ", " : "") +
            (subject != null ? "subject=" + subject + ", " : "") +
            (content != null ? "content=" + content + ", " : "") +
            (image != null ? "image=" + image + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (dataId != null ? "dataId=" + dataId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
