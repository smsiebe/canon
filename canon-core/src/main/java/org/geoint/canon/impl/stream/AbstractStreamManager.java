package org.geoint.canon.impl.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.geoint.canon.impl.codec.HierarchicalCodecResolver;
import org.geoint.canon.spi.stream.UnableToResolveStreamException;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.StreamAlreadyExistsException;

/**
 * Manages the streams for a canon instance.
 * <p>
 * Stream managers 
 * @author steve_siebert
 */
public abstract class AbstractStreamManager {

    private final Map<String, EventStream> streams;
    private final HierarchicalCodecResolver canonCodecs;

    public AbstractStreamManager(HierarchicalCodecResolver canonCodecs,
            Collection<EventStream> streams) {
        this.streams = new HashMap<>();
        this.canonCodecs = canonCodecs;
        streams.forEach((s) -> this.streams.put(s.getName(), s));
    }

    public EventStream getOrCreateStream(String streamName) {
        synchronized (streams) {
            if (streams.containsKey(streamName)) {
                return streams.get(streamName);
            }

            //stream not registered, create a default stream by this name
            return createStream(streamName, Collections.EMPTY_MAP);
        }
    }

    /**
     * Register a new event stream with the canon instance.
     *
     * @param stream stream to register
     * @throws StreamAlreadyExistsException if a stream by this name is already
     * registered to this canon instance
     */
    public void registerStream(EventStream stream)
            throws StreamAlreadyExistsException {
        synchronized (streams) {
            if (streams.containsKey(stream.getName())) {
                throw new StreamAlreadyExistsException(stream.getName());
            }
            streams.put(stream.getName(), stream);
        }
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

    public abstract EventStream createStream(String streamName,
            Map<String, String> streamProperties);

}
