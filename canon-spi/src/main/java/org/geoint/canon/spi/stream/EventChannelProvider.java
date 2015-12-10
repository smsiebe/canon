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
     * Determines if this provider can instantiate a channel.
     *
     * @param scheme
     * @param channelProperties
     * @return unique stream "scheme" name supported by this provider
     */
    boolean provides(String scheme, Map<String, String> channelProperties);

    /**
     * Returns the requested event channel, creating the channel if necessary.
     *
     * @param channelName name of the channel to create
     * @param channelProperties properties of the event channel
     * @return event channel
     * @throws UnableToResolveChannelException thrown if the provider could not
     * resolve the stream as requested
     */
    EventChannel getChannel(String channelName, Map<String, String> channelProperties)
            throws UnableToResolveChannelException;

}
