/*
 * Copyright 2015 geoint.org.
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
package org.geoint.canon.stream;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.geoint.canon.event.UnknownEventException;
import org.geoint.canon.event.AppendedEventMessage;

/**
 * Decorates an EventReader.
 *
 * @author steve_siebert
 */
public abstract class EventReaderDecorator implements EventReader {

    protected final EventReader reader;
    protected String currentEventId;

    private static final long THREAD_WAIT_MILLS = 10L;

    public EventReaderDecorator(EventReader reader) {
        this.reader = reader;
    }

    /**
     * Default implementation delegates call to decorated reader.
     *
     * @return
     */
    @Override
    public boolean hasNext() {
        return reader.hasNext();
    }

    /**
     * This method calls {@link EventReaderDecorator#poll() }, rather than
     * delegating the call to the decorated reader, so that decorator
     * implementations only need to implement 
     * {@link EventReaderDecorator#poll() } for most cases.
     *
     * @param timeout
     * @param unit
     * @return
     * @throws StreamReadException
     * @throws InterruptedException
     */
    @Override
    public Optional<AppendedEventMessage> poll(long timeout, TimeUnit unit)
            throws StreamReadException, InterruptedException {
        long waited = 0;
        Optional<AppendedEventMessage> event = Optional.empty();
        while (!(event = this.poll()).isPresent()
                || unit.toMillis(timeout) > waited) {
            Thread.sleep(THREAD_WAIT_MILLS);
            waited += THREAD_WAIT_MILLS;
        }

        return event;
    }

    /**
     * This method calls {@link EventReaderDecorator#poll() }, rather than
     * delegating the call to the decorated reader, so that decorator
     * implementations only need to implement 
     * {@link EventReaderDecorator#poll() } for most cases.
     *
     * @return
     * @throws StreamReadException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Override
    public AppendedEventMessage take()
            throws StreamReadException, TimeoutException, InterruptedException {
        Optional<AppendedEventMessage> event = null;
        while (!(event = this.poll()).isPresent()) {
            Thread.sleep(THREAD_WAIT_MILLS);
        }
        return event.get();
    }

    /**
     * Default implementation delegates call to decorated reader.
     *
     * @param eventId
     * @throws UnknownEventException
     */
    @Override
    public void setPosition(String eventId) throws UnknownEventException {
        this.currentEventId = eventId;
        reader.setPosition(eventId);
    }

    /**
     * Default implementation delegates call to decorated reader.
     *
     * @return
     */
    @Override
    public EventStream getStream() {
        return reader.getStream();
    }

    /**
     * Default implementation delegates call to decorated reader.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        reader.close();
    }

}
