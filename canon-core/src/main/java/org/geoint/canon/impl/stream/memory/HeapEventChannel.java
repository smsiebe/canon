package org.geoint.canon.impl.stream.memory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.geoint.canon.stream.EventChannel;
import org.geoint.canon.stream.EventStream;

/**
 *
 * @author steve_siebert
 */
public class HeapEventChannel implements EventChannel {

    private final String name;
    private final Map<String, String> channelProperties;
    private final Map<String, HeapEventStream> streams;

    /**
     * Create a new memory channel without any streams.
     *
     * @param name channel name
     * @param channelProperties
     */
    public HeapEventChannel(String name, Map<String, String> channelProperties) {
        this.name = name;
        this.channelProperties = channelProperties;
        this.streams = new HashMap<>();
    }

    /**
     * Create a new memory channel with the provided streams.
     *
     * @param name
     * @param channelProperties
     * @param streams
     */
    public HeapEventChannel(String name, Map<String, String> channelProperties,
            Map<String, HeapEventStream> streams) {
        this.name = name;
        this.channelProperties = channelProperties;
        this.streams = new HashMap<>(streams); //defensive copy
    }

    /**
     * Create a new memory channel with all streams provided by the Supplier.
     * <p>
     * {@link Supplier.get()} is called until it is exhausted (returns null),
     * any streams with duplicate stream names will overwrite any existing
     * stream.
     *
     * @param name
     * @param channelProperties
     * @param streamSupplier
     */
    public HeapEventChannel(String name, Map<String, String> channelProperties,
            Supplier<HeapEventStream> streamSupplier) {
        this.name = name;
        this.channelProperties = channelProperties;
        this.streams = new HashMap<>();
        HeapEventStream s;
        while ((s = streamSupplier.get()) != null) {
            this.streams.put(s.getName(), s);
        }
    }

    @Override
    public String getChannelName() {
        return name;
    }

    @Override
    public Collection<EventStream> getStreams() {
        return streams.entrySet().stream()
                .map(Entry::getValue)
                .collect(Collectors.toList());
    }

}
