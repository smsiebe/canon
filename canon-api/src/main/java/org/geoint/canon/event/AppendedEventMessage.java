package org.geoint.canon.event;

/**
 * An event message that has been successfully committed to an event stream.
 *
 * @author steve_siebert
 */
public interface AppendedEventMessage extends EventMessage {

    /**
     * Stream-unique event sequence identifier of the appended event message.
     *
     * @return event message identity
     */
    String getSequence();

    /**
     * Length of the event content byte stream.
     *
     * @return event content length
     */
    int getEventLength();
}
