package org.geoint.canon.stream;

import org.geoint.canon.event.EventMessageBuilder;

/**
 * Appends events to a stream.
 *
 * @author steve_siebert
 */
public interface EventAppender {

    /**
     * Returns a message builder used to create a new event.
     *
     * @param eventType
     * @return message builder
     * @throws StreamAppendException thrown if there is a problem appending and
     * event to the contextual stream
     */
    EventMessageBuilder newMessage(String eventType)
            throws StreamAppendException;
}
