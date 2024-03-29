package com.glinboy.app.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.glinboy.app.domain.Token} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TokenDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 64)
    private String name;

    @Size(max = 512)
    private String token;

    @NotNull
    private Boolean disable;

    private Instant createdAt;

    @NotNull
    private Instant deprecateAt;

    @NotNull
    private Integer roles;

    private String createdBy;

    private Instant updatedAt;

    private String updatedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getDeprecateAt() {
        return deprecateAt;
    }

    public void setDeprecateAt(Instant deprecateAt) {
        this.deprecateAt = deprecateAt;
    }

    public Integer getRoles() {
        return roles;
    }

    public void setRoles(Integer roles) {
        this.roles = roles;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TokenDTO)) {
            return false;
        }

        TokenDTO tokenDTO = (TokenDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tokenDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TokenDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", token='" + getToken() + "'" +
            ", disable='" + getDisable() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", deprecateAt='" + getDeprecateAt() + "'" +
            ", roles=" + getRoles() +
            ", createdBy='" + getCreatedBy() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            "}";
    }
}
