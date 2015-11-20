package org.geoint.canon.stream.event;

import java.util.Objects;

/**
 * An Event which indicates that another event must be sanitized from the stream
 * and any downstream system.
 * <p>
 * Upon successful committing of a SanitizeEvent, the SanitizeEvent is committed
 * to to the tail of a stream and event type and content of the event targeted
 * for sanitization is replaced with a EventSanitized event.
 *
 * @see EventSanitized
 * @author steve_siebert
 */
public class SanitizeEvent {

    private final String streamName;
    private final String sanitizedEventId;
    private final String justification;

    public SanitizeEvent(String streamName, String sanitizedEventId,
            String justification) {
        this.streamName = streamName;
        this.sanitizedEventId = sanitizedEventId;
        this.justification = justification;
    }

    public String getStreamName() {
        return streamName;
    }

    public String getSanitizedEventId() {
        return sanitizedEventId;
    }

    public String getJustification() {
        return justification;
    }

    @Override
    public String toString() {
        return String.format("Event '%s' was sanitized on stream '%s':  %s",
                sanitizedEventId, streamName, justification);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.streamName);
        hash = 47 * hash + Objects.hashCode(this.sanitizedEventId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SanitizeEvent other = (SanitizeEvent) obj;
        if (!Objects.equals(this.streamName, other.streamName)) {
            return false;
        }
        if (!Objects.equals(this.sanitizedEventId, other.sanitizedEventId)) {
            return false;
        }
        return true;
    }
}
