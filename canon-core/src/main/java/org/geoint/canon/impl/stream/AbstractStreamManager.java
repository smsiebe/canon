package org.geoint.canon.impl.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.spi.stream.EventStreamProvider;
import org.geoint.canon.spi.stream.UnableToResolveStreamException;
import org.geoint.canon.stream.EventStream;

/**
 * Manages the streams for a canon instance.
 * <p>
 * Stream managers
 *
 * @author steve_siebert
 */
public abstract class AbstractStreamManager {

    private final Map<String, EventStream> streams;
    private final CodecResolver canonCodecs;

    public AbstractStreamManager(CodecResolver canonCodecs,
            Collection<EventStream> streams) {
        this.streams = new HashMap<>();
        this.canonCodecs = canonCodecs;
        streams.forEach((s) -> this.streams.put(s.getName(), s));
    }

    public Collection<EventStream> getStreams() {
        return new ArrayList(streams.values());
    }

    public Optional<EventStream> findStream(String streamName) {
        return Optional.ofNullable(streams.get(streamName));
    }

    public boolean streamExists(String streamName) {
        return streams.containsKey(streamName);
    }

    public EventStream getOrCreateStream(String streamName,
            Map<String, String> streamProperties,
            EventStreamProvider provider)
            throws UnableToResolveStreamException {
        synchronized (streams) {
            if (streams.containsKey(streamName)) {
                return streams.get(streamName);
            }

            //stream not registered, create a default stream by this name
            EventStream stream = createStream(streamName, streamProperties, provider);
            streams.put(streamName, stream);
            return stream;
        }
    }

    /**
     * Return the requested event stream, creating a new stream with the default
     * provider if it doesn't already exist.
     *
     * @param streamName stream to retrieve
     * @return stream
     * @throws UnableToResolveStreamException thrown if the stream could not be
     * created
     */
    public EventStream getOrCreateStream(String streamName)
            throws UnableToResolveStreamException {
        return getOrCreateStream(streamName, Collections.EMPTY_MAP,
                getDefaultStreamProvider());
    }

    /**
     * Creates a new event stream, using the event stream provider, after
     * applying stream manager default stream properties. This method does not
     * register the stream with the manager.
     *
     * @param streamName
     * @param streamProperties
     * @param provider
     * @return Created event stream
     * @throws UnableToResolveStreamException
     */
    public EventStream createStream(String streamName,
            Map<String, String> streamProperties, EventStreamProvider provider)
            throws UnableToResolveStreamException {

        //add default properties
        Map<String, String> streamPropertiesCopy
                = new HashMap<>(streamProperties); //defensive copy
        getDefaultStreamProperties()
                .forEach((k, v) -> streamPropertiesCopy.putIfAbsent(k, v));

        return provider.getStream(streamName, streamPropertiesCopy, canonCodecs);
    }

    /**
     * Return any default stream properties defined by this manager.
     *
     * @return default stream properties
     */
    protected abstract Map<String, String> getDefaultStreamProperties();

    /**
     * Return the default stream provider used by this manager.
     *
     * @return default stream provider
     */
    protected abstract EventStreamProvider getDefaultStreamProvider();

}
