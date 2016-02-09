package org.geoint.canon.stream.memory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.impl.stream.AbstractEventChannel;
import org.geoint.canon.stream.ChannelInitializationException;
import org.geoint.canon.stream.EventAppender;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.StreamAlreadyExistsException;
import org.geoint.canon.stream.StreamAppendException;
import org.geoint.canon.stream.event.ChannelCreated;
import org.geoint.canon.stream.event.StreamCreated;

/**
 * Events stored on heap memory.
 *
 * @author steve_siebert
 */
public class HeapEventChannel extends AbstractEventChannel {

    private final Map<String, EventStream> streams;

    private static final Logger LOGGER
            = Logger.getLogger(HeapEventChannel.class.getName());

    /**
     * Create a new memory channel.
     *
     * @param name channel name
     * @param channelProperties properties
     * @param codecs codecs
     * @throws ChannelInitializationException thrown if the channel could not be
     * initialized
     */
    public HeapEventChannel(String name,
            Map<String, String> channelProperties,
            CodecResolver codecs) throws ChannelInitializationException {
        super(name, channelProperties, codecs);

        //every in-memory channel is a new channel, so initialize as such
        this.streams = new HashMap<>();
        EventStream adminStream = new HeapEventStream(this, CHANNEL_ADMIN_STREAM_NAME, codecs);
        this.streams.put(CHANNEL_ADMIN_STREAM_NAME, adminStream);

        //publish channel and admin stream creation events
        try {
            EventAppender appender = adminStream.newAppender();
            appender.create(ChannelCreated.class.getName())
                    .event(new ChannelCreated(name));
            appender.create(StreamCreated.class.getName())
                    .event(new StreamCreated(name, CHANNEL_ADMIN_STREAM_NAME));
            appender.append();
        } catch (StreamAppendException ex) {
            throw new ChannelInitializationException(name, String.format(
                    "Channel '%s' could not be initialized, unable to write to "
                    + "channel admin stream.", name), ex);
        }
    }

    @Override
    public Collection<EventStream> listStreams() {
        return streams.entrySet().stream()
                .map(Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    protected EventStream createStream(String streamName, CodecResolver codecs)
            throws StreamAlreadyExistsException {
        synchronized (streams) {
            if (streams.containsKey(streamName)) {
                throw new StreamAlreadyExistsException(String.format("Stream "
                        + "'%s' already exists on heap channel '%s'",
                        streamName, this.getChannelName()));
            }
            HeapEventStream s = new HeapEventStream(this, streamName, this.codecs);
            streams.put(streamName, s);
            return s;
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (streams) {
            streams.entrySet().stream()
                    .map(Map.Entry::getValue)
                    .forEach((s) -> {
                        try {
                            s.close();
                        } catch (IOException ex) {
                            LOGGER.log(Level.SEVERE,
                                    String.format("Stream '%s-%s' did not "
                                            + "close gracefully.",
                                            getChannelName(), s.getName()), ex);
                        }
                    });
        }
    }

}
