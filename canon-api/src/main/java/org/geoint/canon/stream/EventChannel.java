package org.geoint.canon.stream;

import java.util.Collection;
import java.util.Optional;
import org.geoint.canon.codec.EventCodec;

/**
 * A grouping of related event streams.
 *
 * @author steve_siebert
 */
public interface EventChannel {

    /**
     * Unique name of the channel.
     *
     * @return channel name
     */
    String getChannelName();

    /**
     * Stream used by the channel to manage its own administration.
     * 
     * @return channel admin stream
     */
    EventStream getChannelAdminStream();
    
    /**
     * Retrieve the streams of this channel.
     * <p>
     * Modifications to the returned collection will not change the channel
     * state.
     *
     * @return streams of the channel
     */
    Collection<EventStream> listStreams();

    /**
     * Return the stream if it already exists, or return null.
     *
     * @param streamName stream name to retrieve from channel
     * @return event stream or null if stream does not already exist
     */
    Optional<EventStream> findStream(String streamName);

    /**
     * Returns a stream by this name in this channel, creating the stream if it
     * does not exist.
     *
     * @param streamName name of stream
     * @return named stream within this channel
     * @throws StreamInitializationException thrown if the requested stream 
     * did not exist on the channel and could not be created
     */
    EventStream getOrCreateStream(String streamName) 
            throws StreamInitializationException;

    /**
     * Add a codec to use with this channel and all streams of this channel.
     *
     * @param codec event codec
     */
    void useCodec(EventCodec codec);

}
