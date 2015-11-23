package org.geoint.canon.impl.stream;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.event.CommittedEventMessage;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.event.EventMessageBuilder;
import org.geoint.canon.impl.codec.HierarchicalCodecResolver;
import org.geoint.canon.impl.concurrent.CompletedFuture;
import org.geoint.canon.impl.tx.DefaultEventTransaction;
import org.geoint.canon.impl.tx.EventTransactionFactory;
import org.geoint.canon.stream.EventAppender;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.StreamAppendException;
import org.geoint.canon.tx.EventTransaction;
import org.geoint.canon.tx.EventTransactionException;
import org.geoint.canon.tx.TransactionCommitted;
import org.geoint.canon.tx.TransactionRolledBack;

/**
 * Provides default method implementations for basic appender capabilities,
 * simplifying implementations.
 *
 * @author steve_siebert
 */
public abstract class AbstractEventAppender implements EventAppender {

    protected final EventStream stream;
    protected final EventTransaction tx;
    protected final HierarchicalCodecResolver codecs;

    public AbstractEventAppender(EventStream stream) {
        this.stream = stream;
        this.tx = new DefaultEventTransaction(this::doCommit, this::doRollback);
        this.codecs = new HierarchicalCodecResolver(stream);
    }

    public AbstractEventAppender(EventStream stream,
            EventTransactionFactory txFactory) {
        this.stream = stream;
        this.tx = txFactory.newTransaction();
        this.codecs = new HierarchicalCodecResolver(stream);
    }

    @Override
    public EventMessageBuilder create(String eventType)
            throws StreamAppendException {
        AppendingEventMessageBuilder mb = new AppendingEventMessageBuilder();
        this.append(mb);
        return mb;
    }

    @Override
    public EventAppender append(EventMessage message)
            throws StreamAppendException {
        synchronized (tx) {
            if (!tx.isActive()) {
                throw new StreamAppendException(stream.getName(), "Unable to append "
                        + "event, transaction is not active.");
            }

            if (!codecs.getCodec(message.getEventType()).isPresent()) {
                throw new StreamAppendException(stream.getName(), String.format(
                        "Unable to serialize event type '%s', no codec found",
                        message.getEventType()));
            }

            addToTransaction(message);
        }
        return this;
    }

    @Override
    public EventAppender useCodec(EventCodec codec) {
        codecs.add(codec);
        return this;
    }

    @Override
    public Future<TransactionCommitted> commit()
            throws EventTransactionException {
        synchronized (tx) {
            if (!tx.isActive()) {
                if (tx.isCommitted()) {
                    return CompletedFuture.completed((TransactionCommitted) tx.getResult());
                }

                throw new EventTransactionException(String.format("Unable to commit "
                        + "transaction %s on stream %s, transaction is not "
                        + "active.", tx.getId(), stream.getName()));
            }

            //commit transaction, which in turn calls doCommit
            return tx.commit();
        }
    }

    @Override
    public Future<TransactionRolledBack> rollback()
            throws EventTransactionException {
        synchronized (tx) {
            if (!tx.isActive()) {
                if (tx.isRollback()) {
                    return CompletedFuture.completed((TransactionRolledBack) tx.getResult());
                }

                throw new EventTransactionException(String.format("Unable to "
                        + "rollback transaction %s on stream %s, transaction is "
                        + "not active.", tx.getId(), stream.getName()));
            }

            //rollback transaction, which in turn calls doRollback
            return tx.rollback();
        }
    }

    /**
     * Adds the event to the sequence of events that will be appended upon
     * commit.
     *
     * @param event
     */
    protected abstract void addToTransaction(EventMessage event);

    /**
     * Appends events to the stream.
     *
     * @return
     * @throws StreamAppendException
     */
    protected abstract Collection<CommittedEventMessage> doCommit()
            throws StreamAppendException;

    /**
     * Rolls back any events associated with the transaction.
     *
     * @throws StreamAppendException
     */
    protected abstract void doRollback() throws StreamAppendException;

    /**
     * Event message exposing the buildable interface with the ability of being
     * added to the appender event sequence.
     */
    private class AppendingEventMessageBuilder
            implements EventMessageBuilder, EventMessage {

        private final String eventType;
        private String authorizedBy;
        private Set<String> triggeredBy;
        private Map<String, String> headers;
        private InputStream eventContent;

        public AppendingEventMessageBuilder(String eventType) {
            this.eventType = eventType;
            this.triggeredBy = new HashSet<>();
            this.headers = new HashMap<>();
        }

        @Override
        public EventMessageBuilder authorizedBy(String authorizerId) {
            this.authorizedBy = authorizedBy;
            return this;
        }

        @Override
        public EventMessageBuilder triggeredBy(String eventId) {
            this.triggeredBy.add(eventId);
            return this;
        }

        @Override
        public EventMessageBuilder header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        @Override
        public void event(InputStream event) {

        }

        @Override
        public void event(Object event) {

        }

        @Override
        public void event(Object event, EventCodec<?> codec) {

        }

        @Override
        public String[] getTriggerIds() {

        }

        @Override
        public String getAuthorizerId() {

        }

        @Override
        public String getStreamName() {

        }

        @Override
        public String getEventType() {

        }

        @Override
        public Map<String, String> getHeaders() {

        }

        @Override
        public Optional<String> findHeader(String headerName) {

        }

        @Override
        public String getHeader(String headerName, Supplier<String> defaultValue) {

        }

        @Override
        public InputStream getEventContent() {

        }

        @Override
        public int getEventLength() {

        }

        @Override
        public <E> E getEvent(EventCodec<E> codec) {

        }

    }
}
