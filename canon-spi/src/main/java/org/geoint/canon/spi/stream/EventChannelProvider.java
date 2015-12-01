package org.geoint.canon.spi.stream;

import java.util.Map;
import org.geoint.canon.stream.EventChannel;

/**
 * Creates an EventStream for a specific transport type as defined by the scheme
 * component of the stream URL.
 *
 * @author steve_siebert
 */
public interface EventChannelProvider {

    /**
     * Indicates if this provider can create streams for the user-defined stream
     * URI scheme.
     *
     * @return unique stream "scheme" name supported by this provider
     */
    String getScheme();

    /**
     * Returns the requested event channel.
     *
     * @param channelName name of the channel to create
     * @param channelProperties properties of the event channel
     * @return event channel
     * @throws UnableToResolveStreamException thrown if the provider could not
     * resolve the stream as requested
     */
    EventChannel getChannel(String channelName, Map<String, String> channelProperties)
            throws UnableToResolveStreamException;

}
