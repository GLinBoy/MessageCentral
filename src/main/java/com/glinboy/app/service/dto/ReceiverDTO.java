package com.glinboy.app.service.dto;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ReceiverDTO implements Serializable {

    @NotNull
    @Size(max = 64)
    private String username;

    @NotNull
    @Size(max = 164)
    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, username);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReceiverDTO other = (ReceiverDTO) obj;
        return Objects.equals(token, other.token) && Objects.equals(username, other.username);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ReceiverDTO [username=");
        builder.append(username);
        builder.append(", token=");
        builder.append(token);
        builder.append("]");
        return builder.toString();
    }

}
