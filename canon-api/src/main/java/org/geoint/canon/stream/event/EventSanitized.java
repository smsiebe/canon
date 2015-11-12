package org.geoint.canon.stream.event;

import java.util.Objects;

/**
 * Event identifying an event on a stream which must be deleted from the domain.
 *
 * @author steve_siebert
 */
public class EventSanitized {

    private final String streamName;
    private final String sanitizedEventId;
    private final String justification;

    public EventSanitized(String streamName, String sanitizedEventId,
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
        final EventSanitized other = (EventSanitized) obj;
        if (!Objects.equals(this.streamName, other.streamName)) {
            return false;
        }
        if (!Objects.equals(this.sanitizedEventId, other.sanitizedEventId)) {
            return false;
        }
        return true;
    }

}
