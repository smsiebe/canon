package org.geoint.canon.impl.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.codec.CodecNotFoundException;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.codec.EventCodecException;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.event.EventMessageBuilder;
import org.geoint.canon.impl.codec.HierarchicalCodecResolver;
import org.geoint.canon.impl.codec.PipedEventEncoder;
import org.geoint.canon.impl.concurrent.CompletedFuture;
import org.geoint.canon.stream.EventAppender;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.StreamAppendException;
import org.geoint.canon.stream.EventsAppended;
import org.geoint.canon.event.AppendedEventMessage;

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

    private final Logger LOGGER = Logger.getLogger(EventAppender.class.getName());

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
        AppendingEventMessageBuilder mb
                = new AppendingEventMessageBuilder(eventType);
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
    public Future<EventsAppended> commit()
            throws EventTransactionException {
        synchronized (tx) {
            if (!tx.isActive()) {
                if (tx.isCommitted()) {
                    return CompletedFuture.completed((EventsAppended) tx.getResult());
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
    protected abstract Collection<AppendedEventMessage> doCommit()
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
        private final Set<String> triggeredBy;
        private final Map<String, String> headers;
        private Supplier<InputStream> eventContent;

        public AppendingEventMessageBuilder(String eventType) {
            this.eventType = eventType;
            this.triggeredBy = new HashSet<>();
            this.headers = new HashMap<>();
        }

        @Override
        public EventMessageBuilder authorizedBy(String authorizerId) {
            this.authorizedBy = authorizerId;
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
            this.eventContent = () -> event;
        }

        @Override
        public void event(Object event) throws EventCodecException {
            event(event, codecs.getCodec(eventType).get());
        }

        @Override
        public void event(final Object event, EventCodec<?> codec)
                throws EventCodecException {
            final EventCodec encodingCodec;
            if (codec == null) {
                Optional<EventCodec> c = codecs.getCodec(eventType);
                encodingCodec = (c.orElseThrow(() -> new CodecNotFoundException(
                        "Unable to find codec for event '%s'", eventType)));
            } else {
                encodingCodec = codec;
            }

            if (!encodingCodec.getSupportedEventType().contentEquals(eventType)) {
                throw new EventCodecException(String.format("Codec '%s' cannot "
                        + "be used to encode event type '%s'",
                        encodingCodec.getClass().getCanonicalName(),
                        eventType));
            }
            
            this.eventContent = () -> {
                try {
                    return new PipedEventEncoder(encodingCodec).encode(event);
                } catch (EventCodecException ex) {
                    LOGGER.log(Level.WARNING, "Event cannot be encoded", ex);
                    //set the event content to a supplier that will return an 
                    //inputstream that will always throw the codec exception
                    return new ExceptionalInputStream(ex);
                }
            };
        }

        @Override
        public String[] getTriggerIds() {
            return triggeredBy.toArray(new String[triggeredBy.size()]);
        }

        @Override
        public String getAuthorizerId() {
            return authorizedBy;
        }

        @Override
        public String getStreamName() {
            return stream.getName();
        }

        @Override
        public String getEventType() {
            return eventType;
        }

        @Override
        public Map<String, String> getHeaders() {
            return Collections.unmodifiableMap(headers);
        }

        @Override
        public Optional<String> findHeader(String headerName) {
            return Optional.ofNullable(headers.get(headerName));
        }

        @Override
        public String getHeader(String headerName, Supplier<String> defaultValue) {
            return findHeader(headerName).orElseGet(defaultValue);
        }

        @Override
        public InputStream getEventContent() {
            return eventContent.get();
        }

        @Override
        public <E> E getEvent(EventCodec<E> codec) 
                throws IOException, EventCodecException {
            return codec.decode(this);
        }

    }

    /**
     * Will always throw an IOException with the the provided exception as the
     * causedBy for any method called on the InputStream.
     */
    private class ExceptionalInputStream extends InputStream {

        private final IOException ex;

        public ExceptionalInputStream(Throwable ex) {
            this.ex = new IOException(ex);
        }

        @Override
        public int read() throws IOException {
            throw ex;
        }

    }
}
