package com.glinboy.app.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Token.
 */
@Entity
@Table(name = "token")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Token implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 64)
    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @NotNull
    @Size(max = 512)
    @Column(name = "token", length = 512, nullable = false)
    private String token;

    @NotNull
    @Column(name = "disable", nullable = false)
    private Boolean disable;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "deprecate_at", nullable = false)
    private Instant deprecateAt;

    @NotNull
    @Column(name = "roles", nullable = false)
    private Integer roles;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Token id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Token name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return this.token;
    }

    public Token token(String token) {
        this.setToken(token);
        return this;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getDisable() {
        return this.disable;
    }

    public Token disable(Boolean disable) {
        this.setDisable(disable);
        return this;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Token createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getDeprecateAt() {
        return this.deprecateAt;
    }

    public Token deprecateAt(Instant deprecateAt) {
        this.setDeprecateAt(deprecateAt);
        return this;
    }

    public void setDeprecateAt(Instant deprecateAt) {
        this.deprecateAt = deprecateAt;
    }

    public Integer getRoles() {
        return this.roles;
    }

    public Token roles(Integer roles) {
        this.setRoles(roles);
        return this;
    }

    public void setRoles(Integer roles) {
        this.roles = roles;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Token)) {
            return false;
        }
        return id != null && id.equals(((Token) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Token{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", token='" + getToken() + "'" +
            ", disable='" + getDisable() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", deprecateAt='" + getDeprecateAt() + "'" +
            ", roles=" + getRoles() +
            "}";
    }
}