package com.glinboy.app.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.glinboy.app.domain.Token} entity. This class is used
 * in {@link com.glinboy.app.web.rest.TokenResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tokens?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class TokenCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter token;

    private BooleanFilter disable;

    private InstantFilter createdAt;

    private InstantFilter deprecateAt;

    private IntegerFilter roles;

    private Boolean distinct;

    public TokenCriteria() {}

    public TokenCriteria(TokenCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.token = other.token == null ? null : other.token.copy();
        this.disable = other.disable == null ? null : other.disable.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.deprecateAt = other.deprecateAt == null ? null : other.deprecateAt.copy();
        this.roles = other.roles == null ? null : other.roles.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TokenCriteria copy() {
        return new TokenCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
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

    public BooleanFilter getDisable() {
        return disable;
    }

    public BooleanFilter disable() {
        if (disable == null) {
            disable = new BooleanFilter();
        }
        return disable;
    }

    public void setDisable(BooleanFilter disable) {
        this.disable = disable;
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

    public InstantFilter getDeprecateAt() {
        return deprecateAt;
    }

    public InstantFilter deprecateAt() {
        if (deprecateAt == null) {
            deprecateAt = new InstantFilter();
        }
        return deprecateAt;
    }

    public void setDeprecateAt(InstantFilter deprecateAt) {
        this.deprecateAt = deprecateAt;
    }

    public IntegerFilter getRoles() {
        return roles;
    }

    public IntegerFilter roles() {
        if (roles == null) {
            roles = new IntegerFilter();
        }
        return roles;
    }

    public void setRoles(IntegerFilter roles) {
        this.roles = roles;
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
        final TokenCriteria that = (TokenCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(token, that.token) &&
            Objects.equals(disable, that.disable) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(deprecateAt, that.deprecateAt) &&
            Objects.equals(roles, that.roles) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, token, disable, createdAt, deprecateAt, roles, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TokenCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (token != null ? "token=" + token + ", " : "") +
            (disable != null ? "disable=" + disable + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (deprecateAt != null ? "deprecateAt=" + deprecateAt + ", " : "") +
            (roles != null ? "roles=" + roles + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
