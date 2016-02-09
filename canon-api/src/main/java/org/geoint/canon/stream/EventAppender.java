package org.geoint.canon.stream;

import org.geoint.canon.stream.event.EventsAppended;
import java.util.concurrent.Future;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.event.EventMessageBuilder;

/**
 * Appends one or more events to an event stream as a single operation.
 *
 * @author steve_siebert
 */
public interface EventAppender {

    /**
     * Returns a message builder used to create a new event.
     *
     * @param eventType type of event
     * @return message builder
     * @throws StreamAppendException thrown if there is a problem appending and
     * event to the contextual stream
     */
    EventMessageBuilder create(String eventType)
            throws StreamAppendException;

    /**
     * Append the event message in the order in which it is added to this
     * appender.
     *
     * @param message message to append to the stream
     * @return this appender (fluid interface)
     * @throws StreamAppendException thrown if there is a problem appending and
     * event to the contextual stream
     */
    EventAppender add(EventMessage message)
            throws StreamAppendException;

    /**
     * Assigns a specific to use to read/write event payloads on this stream.
     * <p>
     * Set an event codec on an appender will take precedence over any matching
     * codec set at at a higher level.
     *
     * @param codec event codec
     * @return this appender (fluid interface)
     */
    EventAppender useCodec(EventCodec codec);

    /**
     * Appends all events added to this appender to the event stream.
     *
     * @return transaction results
     * @throws StreamAppendException thrown if there was a problem appending the
     * events to the stream
     */
    EventsAppended append() throws StreamAppendException;

}
