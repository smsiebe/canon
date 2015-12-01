package org.geoint.canon.impl.stream.memory;

import java.util.Map;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.spi.stream.UnableToResolveStreamException;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.spi.stream.EventChannelProvider;

/**
 * Provider of volatile, in-memory, event streams that will be destroyed on JVM
 * shutdown.
 *
 * @author steve_siebert
 */
public class MemoryStreamProvider implements EventChannelProvider {

    public static final String SCHEME = "mem";

    @Override
    public String getScheme() {
        return SCHEME;
    }

    @Override
    public EventStream getStream(String streamName,
            Map<String, String> streamProperties,
            CodecResolver codecs) throws UnableToResolveStreamException {
        return new MemoryChannelStream(streamName, streamProperties, codecs);
    }

}
