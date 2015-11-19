package org.geoint.canon.spi.stream;

import java.util.Map;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.stream.EventStream;

/**
 * Creates an EventStream for a specific transport type as defined by the scheme
 * component of the stream URL.
 *
 * @author steve_siebert
 */
public interface EventStreamProvider {

    /**
     * Indicates if this provider can create streams for the user-defined stream
     * URI scheme.
     *
     * @return unique stream "scheme" name supported by this provider
     */
    String getScheme();

    /**
     * Returns the requested event stream.
     *
     * @param streamName name of the stream to create
     * @param streamProperties properties of the event stream
     * @param codecs codecs available to the stream
     * @return event stream
     * @throws UnableToResolveStreamException thrown if the provider could not
     * resolve the stream as requested
     */
    EventStream getStream(String streamName,
            Map<String, String> streamProperties,
            CodecResolver codecs)
            throws UnableToResolveStreamException;

}
