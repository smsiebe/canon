package org.geoint.canon.stream;

import java.util.Collection;
import java.util.Map;

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
     * Retrieve the streams of this channel.
     * <p>
     * Modifications to the returned collection will not change the channel
     * state.
     *
     * @return streams of the channel
     */
    Collection<EventStream> getStreams();

    /**
     * Returns a stream by this name in this channel, creating the stream if it
     * does not exist.
     *
     * @param streamName
     * @param streamProperties
     * @return named stream within this channel
     */
    EventStream getStream(String streamName, Map<String, String> streamProperties);

}
