package org.geoint.canon.stream.event;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Event which replaces an event that was sanitized.
 * <p>
 * This event is unique in that it provides a reference to an event that exists
 * later in the stream.
 *
 * @see SanitizeEvent
 * @author steve_siebert
 */
public class EventSanitized {

    private final String streamName;
    private final String futureSanitizingEvent;
    private final ZonedDateTime dateTimeSanitized;

    public EventSanitized(String streamName, String futureSanitizingEvent,
            ZonedDateTime dateTimeSanitized) {
        this.streamName = streamName;
        this.futureSanitizingEvent = futureSanitizingEvent;
        this.dateTimeSanitized = dateTimeSanitized;
    }

    public String getFutureSanitizingEvent() {
        return futureSanitizingEvent;
    }

    public String getStreamName() {
        return streamName;
    }

    public ZonedDateTime getDateTimeSanitized() {
        return dateTimeSanitized;
    }

    @Override
    public String toString() {
        return String.format("This event was sanitized by event %s on %s",
                futureSanitizingEvent,
                dateTimeSanitized.toString());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.futureSanitizingEvent);
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
        if (!Objects.equals(this.futureSanitizingEvent, other.futureSanitizingEvent)) {
            return false;
        }
        return true;
    }

}
