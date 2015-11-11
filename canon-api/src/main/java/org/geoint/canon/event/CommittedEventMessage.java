package org.geoint.canon.event;

/**
 * An event message that has been successfully committed to an event stream.
 *
 * @author steve_siebert
 */
public interface CommittedEventMessage extends EventMessage {

    /**
     * Universally unique event id.
     *
     * @return committed unique id of the event message
     */
    String getId();

    /**
     * The event ID of the previous event in the sequence.
     *
     * @return previous event id in the sequence
     */
    String getPreviousEventId();
}
