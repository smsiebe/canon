package org.geoint.canon.impl.stream.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.event.AppendedEventMessage;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.event.UnknownEventException;
import org.geoint.canon.impl.concurrent.CompletedFuture;
import org.geoint.canon.impl.stream.AbstractEventAppender;
import org.geoint.canon.impl.stream.AbstractEventStream;
import org.geoint.canon.stream.EventAppender;
import org.geoint.canon.stream.EventReader;
import org.geoint.canon.stream.event.EventsAppended;
import org.geoint.canon.stream.StreamAppendException;
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
    private final List<AppendedEventMessage> messages;
    //message collection lock and synchronizer for wait/notify of messages
    private final ReentrantReadWriteLock msgLock = new ReentrantReadWriteLock();

    public HeapEventStream(String channelName, String streamName,
            Map<String, String> streamProperties,
            ExecutorService executor) {
        super(channelName, streamName, streamProperties, executor);
        this.messages = new ArrayList<>();
    }

    public HeapEventStream(String channelName,
            String streamName, Map<String, String> streamProperties,
            ExecutorService executor,
            Collection<AppendedEventMessage> messages) {
        super(channelName, streamName, streamProperties, executor);
        //defensive copy
        this.messages = new ArrayList<>(messages);
    }

    @Override
    public EventReader newReader() {
        return new MemoryEventReader();
    }

    @Override
    public EventAppender newAppender() {
        return new MemoryEventAppender(channelName, streamName, streamCodecs);
    }

    private class MemoryEventAppender extends AbstractEventAppender {

        private final List<EventMessage> appenderMessages;

        public MemoryEventAppender(String channelName, String streamName,
                CodecResolver codecs) {
            super(channelName, streamName, codecs);
            appenderMessages = Collections.synchronizedList(new ArrayList<>());
        }

        @Override
        public EventAppender add(EventMessage message)
                throws StreamAppendException {
            appenderMessages.add(message);
            return this;
        }

        @Override
        public Future<EventsAppended> append() throws StreamAppendException {
            Lock lock = msgLock.writeLock();
            try {
                lock.lock();

                AppendedEventMessage[] appendedMessages
                        = new AppendedEventMessage[appenderMessages.size()];
                for (int i = 0; i < appenderMessages.size(); i++) {
                    EventMessage msg = appenderMessages.get(i);
                    appendedMessages[i] = HeapAppendedEventMessage.fromMessage(
                            sequencer.next(msg), msg);
                }

                return CompletedFuture.completed(
                        new EventsAppended(streamName, appendedMessages)
                );

            } finally {
                lock.unlock();
            }

        }

    }

    /**
     * Reader of an in-memory event stream.
     */
    private class MemoryEventReader implements EventReader {

        private volatile int currentIndex;
        private String currentSequence;
        
        @Override
        public boolean hasNext() {
            Lock lock = msgLock.readLock();

            try {
                lock.lock();
                return messages.size() > (currentIndex + 1);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public Optional<AppendedEventMessage> poll() throws StreamReadException {
            Lock lock = msgLock.readLock();

            try {
                lock.lock();
                try {
                    AppendedEventMessage msg = messages.get(currentIndex + 1);
                    currentIndex++;
                    currentSequence = msg.getSequence();
                    return Optional.of(msg);
                } catch (IndexOutOfBoundsException ex) {
                    return Optional.empty();
                }
            } finally {
                lock.unlock();
            }
        }

        @Override
        public Optional<AppendedEventMessage> poll(final long timeout,
                TimeUnit unit) throws StreamReadException, InterruptedException {
            Lock lock = msgLock.readLock();
            try {
                lock.lock();
                Optional<AppendedEventMessage> msg = poll();

                if (msg.isPresent()) {
                    return msg;
                }

                //no message, wait on message synchronizer
                synchronized (msgLock) {
                    msgLock.wait(unit.toMillis(timeout));
                }

                return poll();
            } finally {
                lock.unlock();
            }

        }

        @Override
        @SuppressWarnings("SleepWhileInLoop")
        public AppendedEventMessage take()
                throws StreamReadException, InterruptedException {

            Lock lock = msgLock.readLock();

            try {
                lock.lock();
                Optional<AppendedEventMessage> msg = poll();

                if (msg.isPresent()) {
                    return msg.get();
                }

                //no message, wait on message synchronizer
                synchronized (msgLock) {
                    msgLock.wait();
                }

                return poll().orElse(null);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void setPosition(String sequence) throws UnknownEventException {
            Lock lock = msgLock.writeLock();
            try {
                lock.lock();
                
            } finally {
                lock.unlock();
            }
        }

        @Override
        public String getPosition() {
            Lock lock = msgLock.readLock();
            try {
                lock.lock();
                return currentSequence;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void close()  {
        }

    }
}
