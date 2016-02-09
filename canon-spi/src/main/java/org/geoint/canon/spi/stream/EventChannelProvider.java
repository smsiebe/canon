package org.geoint.canon.spi.stream;

import java.util.Map;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.stream.ChannelInitializationException;
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
     * Returns the requested event channel or attempts to create it if it does
     * not yet exist.
     *
     * @param channelName name of the channel to create
     * @param channelProperties properties of the event channel
     * @param codecs codecs inherited from the canon instance
     * @return event channel if it already exists, otherwise null
     * @throws UnableToResolveChannelException thrown if the provider could not
     * resolve the stream as requested
     * @throws ChannelInitializationException if the channel could not be
     * initialized by the provider
     */
    EventChannel getChannel(String channelName,
            Map<String, String> channelProperties,
            CodecResolver codecs)
            throws UnableToResolveChannelException,
            ChannelInitializationException;

}
