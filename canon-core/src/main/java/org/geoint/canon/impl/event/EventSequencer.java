package org.geoint.canon.impl.event;

import java.util.Comparator;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.impl.stream.AbstractEventAppender;
import org.geoint.canon.stream.EventAppender;
import org.geoint.canon.stream.StreamAppendException;

/**
 * An EventSequencer is used by the {@link AbstractEventAppender} to determine
 * if an {@link EventMessage} can be appended to the stream, and if so, generate
 * it's unique {@link EventSequence}.
 * <p>
 * EventSequencer implementations must be thread safe.
 *
 * @author steve_siebert
 */
public interface EventSequencer {

    /**
     * Generates the sequence of the next message if the message is to be
     * committed.
     * <p>
     * This operation must not change the state of the event stream/channel, an
     * operation which is reserved for the {@link EventAppender#append()}
     * operation.
     *
     * @param msg message to test if valid in sequence and subject of sequence
     * generation
     * @return message sequence, if the message is valid in context of the
     * stream
     * @throws StreamAppendException thrown if the message cannot be appended
     */
    String next(EventMessage msg) throws StreamAppendException;

    /**
     * Return a thread-safe Comparator that can compare the sequences generated
     * by this sequencer.
     *
     * @return sequencer comparator
     */
    Comparator<String> getComparator();
}
