package org.geoint.canon.stream;

import java.util.Collection;

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
}
