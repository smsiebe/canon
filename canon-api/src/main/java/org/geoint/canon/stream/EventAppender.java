package org.geoint.canon.stream;

import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.event.EventMessageBuilder;
import org.geoint.canon.tx.EventTransactionException;
import org.geoint.canon.tx.TransactionCommitted;
import org.geoint.canon.tx.TransactionRolledBack;

/**
 * Appends one or more events to an event stream as a single, ACID compliant,
 * transaction.
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
    EventMessageBuilder create(String eventType)
            throws StreamAppendException;

    /**
     * Append the event message in the order in which it is added to this
     * appender.
     *
     * @param message message to append to the stream
     * @return this appender (fluid interface)
     */
    EventAppender append(EventMessage message);

    /**
     * Assigns a specific to use to read/write event payloads on this stream.
     * <p>
     * Set an event codec on an appender will take precedence over any matching
     * codec set at at a higher level.
     *
     * @param codec
     * @return this appender (fluid interface)
     */
    EventAppender useCodec(EventCodec codec);

    /**
     * Commit all events added to this appender to the event stream.
     *
     * @return transaction results
     * @throws EventTransactionException thrown if there was a problem
     * committing the transaction, indicating the transaction failed and was
     * rolled back
     */
    TransactionCommitted commit() throws EventTransactionException;

    /**
     * Rollback (remove) all events added to this appender.
     *
     * @return rollback results
     * @throws EventTransactionException thrown if there was a problem rolling
     * back the transaction, but the events will still not be committed to the
     * stream
     */
    TransactionRolledBack rollback() throws EventTransactionException;
}
