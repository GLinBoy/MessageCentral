package com.glinboy.app.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.glinboy.app.domain.Token} entity. This class is used
 * in {@link com.glinboy.app.web.rest.TokenResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tokens?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TokenCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter token;

    private BooleanFilter disable;

    private InstantFilter deprecateAt;

    private IntegerFilter roles;

    private InstantFilter createdAt;

    private StringFilter createdBy;

    private InstantFilter updatedAt;

    private StringFilter updatedBy;

    private Boolean distinct;

    public TokenCriteria() {}

    public TokenCriteria(TokenCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.token = other.optionalToken().map(StringFilter::copy).orElse(null);
        this.disable = other.optionalDisable().map(BooleanFilter::copy).orElse(null);
        this.deprecateAt = other.optionalDeprecateAt().map(InstantFilter::copy).orElse(null);
        this.roles = other.optionalRoles().map(IntegerFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedBy = other.optionalUpdatedBy().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TokenCriteria copy() {
        return new TokenCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getToken() {
        return token;
    }

    public Optional<StringFilter> optionalToken() {
        return Optional.ofNullable(token);
    }

    public StringFilter token() {
        if (token == null) {
            setToken(new StringFilter());
        }
        return token;
    }

    public void setToken(StringFilter token) {
        this.token = token;
    }

    public BooleanFilter getDisable() {
        return disable;
    }

    public Optional<BooleanFilter> optionalDisable() {
        return Optional.ofNullable(disable);
    }

    public BooleanFilter disable() {
        if (disable == null) {
            setDisable(new BooleanFilter());
        }
        return disable;
    }

    public void setDisable(BooleanFilter disable) {
        this.disable = disable;
    }

    public InstantFilter getDeprecateAt() {
        return deprecateAt;
    }

    public Optional<InstantFilter> optionalDeprecateAt() {
        return Optional.ofNullable(deprecateAt);
    }

    public InstantFilter deprecateAt() {
        if (deprecateAt == null) {
            setDeprecateAt(new InstantFilter());
        }
        return deprecateAt;
    }

    public void setDeprecateAt(InstantFilter deprecateAt) {
        this.deprecateAt = deprecateAt;
    }

    public IntegerFilter getRoles() {
        return roles;
    }

    public Optional<IntegerFilter> optionalRoles() {
        return Optional.ofNullable(roles);
    }

    public IntegerFilter roles() {
        if (roles == null) {
            setRoles(new IntegerFilter());
        }
        return roles;
    }

    public void setRoles(IntegerFilter roles) {
        this.roles = roles;
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

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StringFilter getUpdatedBy() {
        return updatedBy;
    }

    public Optional<StringFilter> optionalUpdatedBy() {
        return Optional.ofNullable(updatedBy);
    }

    public StringFilter updatedBy() {
        if (updatedBy == null) {
            setUpdatedBy(new StringFilter());
        }
        return updatedBy;
    }

    public void setUpdatedBy(StringFilter updatedBy) {
        this.updatedBy = updatedBy;
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
        final TokenCriteria that = (TokenCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(token, that.token) &&
            Objects.equals(disable, that.disable) &&
            Objects.equals(deprecateAt, that.deprecateAt) &&
            Objects.equals(roles, that.roles) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(updatedBy, that.updatedBy) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, token, disable, deprecateAt, roles, createdAt, createdBy, updatedAt, updatedBy, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TokenCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalToken().map(f -> "token=" + f + ", ").orElse("") +
            optionalDisable().map(f -> "disable=" + f + ", ").orElse("") +
            optionalDeprecateAt().map(f -> "deprecateAt=" + f + ", ").orElse("") +
            optionalRoles().map(f -> "roles=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalUpdatedBy().map(f -> "updatedBy=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
