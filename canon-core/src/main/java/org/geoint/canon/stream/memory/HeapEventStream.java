package org.geoint.canon.stream.memory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.event.AppendedEventMessage;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.event.UnknownEventException;
import org.geoint.canon.impl.stream.AbstractEventChannel;
import org.geoint.canon.impl.stream.AbstractEventStream;
import org.geoint.canon.impl.stream.MemoryEventAppender;
import org.geoint.canon.stream.EventAppender;
import org.geoint.canon.stream.EventReader;
import org.geoint.canon.stream.event.EventAppended;
import org.geoint.canon.stream.StreamReadException;

/**
 * In-memory event stream storing all events on the heap.
 * <p>
 * This stream implementation is designed to be used for testing purposes only,
 * and likely shouldn't be used in a production system.
 *
 * @author steve_siebert
 */
public class HeapEventStream extends AbstractEventStream {

    //messages on heap
    private final List<AppendedEventMessage> messages
            = Collections.synchronizedList(new LinkedList<>());

    public HeapEventStream(AbstractEventChannel channel, String streamName,
            CodecResolver codecs) {
        super(channel, streamName, codecs);
    }

    public HeapEventStream(AbstractEventChannel channel, String streamName,
            CodecResolver codecs,
            Collection<AppendedEventMessage> messages) {
        this(channel, streamName, codecs);
        this.messages.addAll(messages);
    }

    @Override
    public EventReader newReader() {
        return new MemoryEventReader();
    }

    @Override
    public EventAppender newAppender() {

        return new MemoryEventAppender(channel.getChannelName(),
                streamName,
                streamCodecs,
                (e) -> {

                    //do in-memory conversion to HeapAppendedEventMessage 
                    //so process is complete as a single action
                    AppendedEventMessage[] appendedMessages
                    = new AppendedEventMessage[e.size()];
                    for (int i = 0; i < e.size(); i++) {
                        EventMessage msg = e.get(i);
                        appendedMessages[i] = HeapAppendedEventMessage.fromMessage(
                                channel.generateEventId(msg), msg);
                    }

                    //add all messages to the stream once all codec 
                    //operations are complete
                    synchronized (messages) {
                        Arrays.stream(appendedMessages)
                        .forEach(messages::add);
                    }

                    return new EventAppended(streamName, appendedMessages);

                });
    }

    @Override
    public String getCurrentSequence() {
        return messages.get(messages.size() - 1).getSequence();
    }

    /**
     * Reader of an in-memory event stream.
     */
    private class MemoryEventReader implements EventReader {

        //position is 0-based, so we need to start at -1
        private volatile int currentIndex = -1;

        @Override
        public boolean hasNext() {
            return messages.size() > (currentIndex + 1);
        }

        @Override
        public AppendedEventMessage read() throws StreamReadException {
            return messages.get((currentIndex == -1) ? 0 : currentIndex);
        }

        @Override
        public Optional<AppendedEventMessage> poll() throws StreamReadException {
            try {
                AppendedEventMessage msg = messages.get(currentIndex + 1);
                currentIndex++;
                return Optional.of(msg);
            } catch (IndexOutOfBoundsException ex) {
                return Optional.empty();
            }
        }

        @Override
        public Optional<AppendedEventMessage> poll(final long timeout,
                TimeUnit unit) throws StreamReadException, InterruptedException {
            Optional<AppendedEventMessage> msg = poll();

            if (msg.isPresent()) {
                return msg;
            }

            return poll();

        }

        @Override
        @SuppressWarnings("SleepWhileInLoop")
        public AppendedEventMessage take()
                throws StreamReadException, InterruptedException {

            Optional<AppendedEventMessage> msg = poll();

            if (msg.isPresent()) {
                return msg.get();
            }

            return poll().orElse(null);
        }

        @Override
        public void setPosition(String sequence) throws UnknownEventException {
            for (int i = 0; i < messages.size(); i++) {
                if (messages.get(i).getSequence().contentEquals(sequence)) {
                    this.currentIndex = i;
                    return;
                }
            }
            throw new UnknownEventException(sequence);
        }

        @Override
        public String getPosition() {
            try {
                return read().getSequence();
            } catch (StreamReadException ex) {
                return null;
            }
        }

        @Override
        public void close() {
            messages.clear();
        }

        @Override
        public String getChannelName() {
            return channel.getChannelName();
        }

        @Override
        public String getStreamName() {
            return streamName;
        }

    }
}
