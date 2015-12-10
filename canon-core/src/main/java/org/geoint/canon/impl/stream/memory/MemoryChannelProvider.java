package org.geoint.canon.impl.stream.memory;

import java.util.Map;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.spi.stream.UnableToResolveChannelException;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.spi.stream.EventChannelProvider;
import org.geoint.canon.stream.EventChannel;

/**
 * Provider of volatile, in-memory, event streams that will be destroyed on JVM
 * shutdown.
 *
 * @author steve_siebert
 */
public class MemoryChannelProvider implements EventChannelProvider {

    public static final String SCHEME = "mem";

    @Override
    public String getScheme() {
        return SCHEME;
    }


    @Override
    public EventChannel getChannel(String channelName,
            Map<String, String> channelProperties) 
            throws UnableToResolveChannelException {
        return new HeapEventChannel(channelName, channelProperties);
    }

}
