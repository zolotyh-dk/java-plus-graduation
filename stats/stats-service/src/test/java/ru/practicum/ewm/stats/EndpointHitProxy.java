package ru.practicum.ewm.stats;

import java.util.Objects;

public class EndpointHitProxy extends EndpointHit {

    EndpointHitProxy withNoId() {
        super.setId(null);
        return this;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EndpointHit other)) {
            return false;
        }
        return Objects.equals(this.getId(), other.getId())
                && Objects.equals(this.getApp(), other.getApp())
                && Objects.equals(this.getUri(), other.getUri())
                && Objects.equals(this.getIp(), other.getIp())
                && Objects.equals(this.getTimestamp(), other.getTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getApp(), this.getUri(), this.getIp(), this.getTimestamp());
    }
}
