package com.glinboy.app.domain;

import java.util.Objects;

public class Statistics {
    public Statistics(Long successful, Long failed) {
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
        Statistics statistics = (Statistics) o;
        return successful.equals(statistics.successful) && failed.equals(statistics.failed);
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
