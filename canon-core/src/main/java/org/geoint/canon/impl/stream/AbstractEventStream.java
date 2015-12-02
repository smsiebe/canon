package org.geoint.canon.impl.stream;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.event.EventSequence;
import org.geoint.canon.event.UnknownEventException;
import org.geoint.canon.impl.codec.HierarchicalCodecResolver;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.EventHandler;
import org.geoint.canon.stream.EventReader;
import org.geoint.canon.stream.FilteredEventReader;

/**
 * Implements common event stream methods requiring event stream implementations
 * extending from this class to implement only a subset of methods.
 *
 * @author steve_siebert
 */
public abstract class AbstractEventStream implements EventStream {

    protected final String streamName;
    protected final HierarchicalCodecResolver streamCodecs;
    protected final Map<String, String> streamProperties;
    protected final Map<EventHandler, Future> handlers;
    protected final ExecutorService executor;

    private static final Logger LOGGER
            = Logger.getLogger(EventStream.class.getPackage().getName());

    /**
     *
     * @param streamName
     * @param streamProperties
     * @param codecs
     * @param executor
     */
    public AbstractEventStream(String streamName,
            Map<String, String> streamProperties,
            CodecResolver codecs, ExecutorService executor) {
        this.streamName = streamName;
        this.streamProperties = streamProperties;
        this.streamCodecs = new HierarchicalCodecResolver(codecs);
        this.handlers = new HashMap<>();
        this.executor = executor;
    }

    @Override
    public String getName() {
        return streamName;
    }

    @Override
    public void addHandler(EventHandler handler) {
        //use a default reader, starting at zero
        addHandler(handler, newReader());
    }

    @Override
    public void addHandler(EventHandler handler, Predicate filter) {
        addHandler(handler, new FilteredEventReader(newReader(), filter));
    }

    @Override
    public void addHandler(EventHandler handler, EventSequence sequence)
            throws UnknownEventException {
        EventReader reader = newReader();
        reader.setPosition(sequence);
        addHandler(handler, reader);
    }

    @Override
    public void addHandler(EventHandler handler, Predicate filter,
            EventSequence sequence) throws UnknownEventException {
        EventReader reader = newReader();
        reader.setPosition(sequence);
        addHandler(handler, new FilteredEventReader(reader, filter));
    }

    @Override
    public void addHandler(EventHandler handler, EventReader reader) {

        synchronized (handlers) {
            if (handlers.containsKey(handler)) {
                return;
            }

            HandlerNotifier notifier = new HandlerNotifier(reader, handler);
            handlers.put(handler, executor.submit(notifier));
        }

    }

    @Override
    public void removeHandler(EventHandler handler) {
        HandlerNotifier notifier = null;
        synchronized (handlers) {
             notifier = handlers.remove(handler);
        }
            
        if (notifier != null) {
            //shutdown notifier
            notifier.stop();
        }
    }

    @Override
    public void useCodec(EventCodec codec) {
        this.streamCodecs.add(codec);
    }

    @Override
    public Optional<EventCodec> getCodec(String eventType) {
        return streamCodecs.getCodec(eventType);
    }

}
