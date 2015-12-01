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
     *
     * @return streams of the channel
     */
    Collection<EventStream> getStream();
}
