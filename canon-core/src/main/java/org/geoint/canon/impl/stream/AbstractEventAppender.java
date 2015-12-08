package org.geoint.canon.impl.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.codec.CodecNotFoundException;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.codec.EventCodecException;
import org.geoint.canon.event.AppendedEventMessage;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.event.EventMessageBuilder;
import org.geoint.canon.impl.codec.HierarchicalCodecResolver;
import org.geoint.canon.impl.codec.PipedEventEncoder;
import org.geoint.canon.stream.EventAppender;
import org.geoint.canon.stream.StreamAppendException;
import org.geoint.canon.spi.stream.EventSequence;

/**
 * Provides default method implementations for basic appender capabilities,
 * simplifying implementations.
 *
 * @author steve_siebert
 */
public abstract class AbstractEventAppender implements EventAppender {

    protected final String channelName;
    protected final String streamName;
    protected final HierarchicalCodecResolver codecs;

    private final Logger LOGGER = Logger.getLogger(EventAppender.class.getName());

    public AbstractEventAppender(String channelName, String streamName,
            CodecResolver codecs) {
        this.channelName = channelName;
        this.streamName = streamName;
        this.codecs = new HierarchicalCodecResolver(codecs);
    }

    @Override
    public EventMessageBuilder create(String eventType)
            throws StreamAppendException {
        AppendingEventMessageBuilder mb
                = new AppendingEventMessageBuilder(eventType);
        return mb;
    }

    @Override
    public EventAppender useCodec(EventCodec codec) {
        codecs.add(codec);
        return this;
    }

    /**
     * Buildable event message which adds itself to the appender upon addition
     * of the event content.
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
        public String getChannelName() {
            return channelName;
        }

        @Override
        public EventMessageBuilder authorizedBy(String authorizerId) {
            this.authorizedBy = authorizerId;
            return this;
        }

        @Override
        public EventMessageBuilder triggeredBy(String sequence) {
            this.triggeredBy.add(sequence);
            return this;
        }

        @Override
        public EventMessageBuilder triggeredBy(AppendedEventMessage msg) {
            this.triggeredBy.add(msg.getSequence());
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
        public void event(Object event)
                throws EventCodecException, StreamAppendException {
            event(event, codecs.getCodec(eventType).get());
        }

        @Override
        public void event(final Object event, EventCodec<?> codec)
                throws EventCodecException, StreamAppendException {
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

            add(this);
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
            return streamName;
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
