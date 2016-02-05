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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.event.AppendedEventMessage;
import org.geoint.canon.event.UnknownEventException;

/**
 * An event reader that keeps track of the events which have been successfully
 * read/processed.
 *
 * @author steve_siebert
 */
public abstract class DurableEventReader extends EventReaderDecorator {

    protected final String readerId;
    //value is set if event has been read, otherwise it's null
    protected AppendedEventMessage currentMsg;

    /**
     *
     * @param readerId stream-unique reader identifier
     * @param reader stream reader,
     */
    public DurableEventReader(String readerId, EventReader reader) {
        super(reader);
        this.readerId = readerId;
    }

    /**
     * Stream-unique reader identifier used to durably track the reader
     * position.
     *
     * @return stream-unique reader identifier
     */
    public String getReaderId() {
        return readerId;
    }

    /**
     * Deletes the durable reader.
     * <p>
     * Any further calls to read events from the durable reader will result in a
     * StreamReadException.
     *
     * @throws IOException thrown if the durable reader persistence component
     * could not be deleted - the reader position still remains
     */
    public abstract void deleteReader() throws IOException;

    /**
     * Reads the durable reader position from the backing file.
     *
     * @return reader position stored in the backing file or null if there isn't
     * a position stored
     * @throws IOException thrown if there was a problem reading from the file
     */
    protected abstract String readPosition() throws IOException;

    /**
     * Writes the new position to the backing file.
     *
     * @param position event id
     * @throws IOException thrown if there was a problem writing to the backing
     * file
     */
    protected abstract void writePosition(String position) throws IOException;

    /**
     * Tells the durable reader that the last read event has been successfully
     * processed and allows the durable reader to move to the next event.
     *
     * @throws StreamReadException thrown if the new reader position could not
     * be stored persistently
     */
    public void ok() throws StreamReadException {

        if (currentMsg == null) {
            return;
        }

        try {
            //persist event position
            writePosition(currentMsg.getSequence());
            currentMsg = null;
        } catch (IOException ex) {
            throw new StreamReadException("Unable to update durable reader "
                    + "position.", ex);
        }
    }

    /**
     * Tells the durable reader to skip the last read event, allowing the reader
     * to move to the next event.
     *
     * @throws StreamReadException thrown if the new reader position could not
     * be stored persistently
     */
    public void skip() throws StreamReadException {
        //default action is to do the same as OK
        ok();
    }

    @Override
    public void setPosition(String sequence)
            throws StreamReadException, UnknownEventException {
        final String previousPosition = this.getPosition();
        try {
            reader.setPosition(sequence);
            writePosition(sequence);
            currentMsg = null;
        } catch (IOException ex) {
            reader.setPosition(previousPosition);
            throw new StreamReadException(this.getChannelName(),
                    this.getStreamName(),
                    "Cannot durably change event position.", ex);
        }
    }

    /**
     * Retrieves and auto-acknowledges the next event from the stream, waiting
     * if necessary.
     * <p>
     * If auto-acknowledge is not desired, use the 
     * {@link DurableEventReader#read() } method instead.
     *
     * @return next event in the stream
     * @throws StreamReadException thrown if there is a problem reading events
     * @throws InterruptedException if the waiting thread was interrupted
     */
    @Override
    public AppendedEventMessage take()
            throws StreamReadException, InterruptedException {
        if (currentMsg == null) {
            currentMsg = super.take();
        }

        return currentMsg;
    }

    /**
     * Returns and auto-acknowledges the next event in the stream, waiting up to
     * the specified time for an event.
     * <p>
     * If auto-acknowledge is not desired, use the 
     * {@link DurableEventReader#read() } method instead.
     *
     * @param timeout how long to wait
     * @param unit time unit of timeout
     * @return next event or null if timed out
     * @throws StreamReadException thrown if there is a problem reading events
     * @throws InterruptedException thrown if the blocked thread was interrupted
     */
    @Override
    public Optional<AppendedEventMessage> poll(long timeout, TimeUnit unit)
            throws StreamReadException, InterruptedException {
        if (currentMsg == null) {
            super.poll(timeout, unit).ifPresent((c) -> currentMsg = c);
        }

        return Optional.ofNullable(currentMsg);
    }

    /**
     * Returns and auto-acknowledges the next event in the stream or null if
     * there are no events after the readers current position.
     *
     * @return next event or null
     * @throws StreamReadException thrown if there is a problem reading events
     * from the event stream
     */
    @Override
    public Optional<AppendedEventMessage> poll() throws StreamReadException {
        if (currentMsg == null) {
            super.poll().ifPresent((c) -> currentMsg = c);
        }
        return Optional.ofNullable(currentMsg);
    }

}
