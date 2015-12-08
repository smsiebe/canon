package org.geoint.canon.impl.stream.memory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.event.AppendedEventMessage;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.event.UnknownEventException;
import org.geoint.canon.impl.concurrent.CompletedFuture;
import org.geoint.canon.impl.stream.AbstractEventAppender;
import org.geoint.canon.impl.stream.AbstractEventStream;
import org.geoint.canon.stream.EventAppender;
import org.geoint.canon.stream.EventReader;
import org.geoint.canon.stream.EventStream;
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
    //message sychronization lock
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

    private class MemoryEventReader implements EventReader {

        @Override
        public boolean hasNext() {
Lock lock = msgLock.readLock();

        
            try {
        lock.lock();
        }finally {
        lock.unlock();
        }
        }

        @Override
        public Optional<AppendedEventMessage> poll() throws StreamReadException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Optional<AppendedEventMessage> poll(long timeout, TimeUnit unit) throws StreamReadException, InterruptedException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public AppendedEventMessage take() throws StreamReadException, TimeoutException, InterruptedException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setPosition(String sequence) throws UnknownEventException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public EventStream getStream() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void close() throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        
    }
}
