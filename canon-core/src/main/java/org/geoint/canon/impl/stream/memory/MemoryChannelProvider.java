package org.geoint.canon.impl.stream.memory;

import java.util.Map;
import org.geoint.canon.spi.stream.UnableToResolveChannelException;
import org.geoint.canon.spi.stream.EventChannelProvider;
import org.geoint.canon.stream.EventChannel;

/**
 * Provider of volatile, in-memory, event streams that will be destroyed on JVM
 * shutdown.
 * <p>
 * Currently this provider only supports on-heap memory channels. Future
 * implementations may support other options, such as off-heap, through the use
 * of channel properties.
 *
 * @author steve_siebert
 */
public class MemoryChannelProvider implements EventChannelProvider {

    public static final String SCHEME = "mem";

    @Override
    public boolean provides(String scheme,
            Map<String, String> channelProperties) {
        return SCHEME.equalsIgnoreCase(scheme);
    }

    @Override
    public EventChannel getChannel(String channelName,
            Map<String, String> channelProperties)
            throws UnableToResolveChannelException {
        return new HeapEventChannel(channelName, channelProperties);
    }

}
