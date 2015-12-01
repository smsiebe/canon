package org.geoint.canon.event;

/**
 * An event message that has been successfully committed to an event stream.
 *
 * @author steve_siebert
 */
public interface AppendedEventMessage extends EventMessage {

    /**
     * Universally unique ID of the appended event message.
     * 
     * @return event message identity
     */
    EventSequence getSequence();
    
    /**
     * Length of the event content byte stream.
     *
     * @return event content length
     */
    int getEventLength();
}
