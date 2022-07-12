package com.glinboy.app.domain;

import java.util.Objects;

public class Statics {
    public Statics(Long successful, Long failed) {
        this.successful = successful;
        this.failed = failed;
    }

    private Long successful;
    private Long failed;

    public Long getSuccessful() {
        return successful;
    }

    public void setSuccessful(Long successful) {
        this.successful = successful;
    }

    public Long getFailed() {
        return failed;
    }

    public void setFailed(Long failed) {
        this.failed = failed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statics statics = (Statics) o;
        return successful.equals(statics.successful) && failed.equals(statics.failed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(successful, failed);
    }

    @Override
    public String toString() {
        return "Statics{" +
            "successful=" + successful +
            ", failed=" + failed +
            '}';
    }
}
