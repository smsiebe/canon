/*
 * Copyright 2016 geoint.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geoint.canon.stream.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.geoint.canon.event.AppendedEventMessage;
import org.geoint.canon.event.UnknownEventException;
import org.geoint.canon.stream.EventReader;
import org.geoint.canon.stream.StreamReadException;

/**
 * Mock EventReader implementation for unit testing.
 * <p>
 * This class "skips" the complex channel->stream->reader framework and allows
 * the reader to be directly fed AppendedEventMessage instances that than may be
 * read.
 * <p>
 * This instance is <b>NOT</b> thread-safe.
 *
 * @author steve_siebert
 */
public class MockEventReader implements EventReader {

    private final String channelName;
    private final String streamName;
    private final List<AppendedEventMessage> messages
            = Collections.synchronizedList(new ArrayList<>());
    private int position = -1; //position is 0-based, so we need to initialize at -1

    public MockEventReader(String channelName, String streamName) {
        this.channelName = channelName;
        this.streamName = streamName;
    }

    public MockEventReader(String channelName, String streamName,
            List<AppendedEventMessage> messages) {
        this.channelName = channelName;
        this.streamName = streamName;
        this.messages.addAll(messages);

    }

    @Override
    public String getChannelName() {
        return channelName;
    }

    @Override
    public String getStreamName() {
        return this.streamName;
    }

    @Override
    public boolean hasNext() {
        try {
            AppendedEventMessage msg = messages.get(position + 1);
            return true;
        } catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }

    @Override
    public AppendedEventMessage read() throws StreamReadException {
        return messages.get(position);
    }

    @Override
    public Optional<AppendedEventMessage> poll() throws StreamReadException {

        int nextPos = position + 1;
        try {
            AppendedEventMessage msg = messages.get(nextPos);
            position++;
            return Optional.of(msg);
        } catch (IndexOutOfBoundsException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<AppendedEventMessage> poll(long timeout, TimeUnit unit)
            throws StreamReadException, InterruptedException {
        final long maxTime = System.currentTimeMillis() + unit.toMillis(timeout);
        while (System.currentTimeMillis() < maxTime) {
            Optional<AppendedEventMessage> msg = poll();
            if (msg.isPresent()) {
                return msg;
            }
            Thread.sleep(1);
        }
        return Optional.empty();
    }

    @Override
    public AppendedEventMessage take()
            throws StreamReadException, InterruptedException {
        return poll(10, TimeUnit.DAYS).get();
    }

    @Override
    public void setPosition(String sequence)
            throws StreamReadException, UnknownEventException {
        for (int i = 0; i <= messages.size(); i++) {
            AppendedEventMessage msg = messages.get(i);
            if (msg.getSequence().contentEquals(sequence)) {
                this.position = i;
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
            //in memory, won't get here
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() throws IOException {
        messages.clear();
    }

    /**
     * Adds a random event to this reader.
     */
    public void addRandomEvent() {
        messages.add(MockAppendedEventMessage
                .random(this.channelName, this.streamName)
        );
    }

    public void addRandomEvent(String eventType) {
        messages.add(MockAppendedEventMessage.random(channelName, streamName,
                eventType));
    }

}
