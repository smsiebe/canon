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
package org.geoint.canon.client.stream.reader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.stream.EventReader;
import org.geoint.canon.stream.EventReaderDecorator;
import org.geoint.canon.stream.StreamReadException;

/**
 * Keeps track of the current stream position using a local file for
 * persistence.
 *
 * @author steve_siebert
 * @param <E>
 */
public class FileBackedDurableEventReader<E extends EventMessage>
        extends EventReaderDecorator<E> implements DurableEventReader<E> {

    private final RandomAccessFile raf;

    public FileBackedDurableEventReader(File trackingFile, EventReader<E> reader)
            throws StreamReadException {
        super(reader);

        try {
            if (!trackingFile.exists()) {
                trackingFile.createNewFile();
            }
            this.raf = new RandomAccessFile(trackingFile, "rwd");
        } catch (IOException ex) {
            throw new StreamReadException(
                    reader.getStream().getChannel().getName(),
                    reader.getStream().getName(),
                    "Problems creating durable stream tracking file", ex);
        }
    }

    @Override
    public void ok() throws StreamReadException {
        writeEventId();
    }

    @Override
    public void skip() throws StreamReadException {
        writeEventId();
    }

    private void writeEventId() throws StreamReadException {
        try {
            //TODO make this operation more resilient to corruption/failure
            raf.setLength(0);
            raf.write(currentEventId.getBytes());
        } catch (IOException ex) {
            throw new StreamReadException(reader.getStream().getChannel().getName(),
                    reader.getStream().getName(),
                    "Unable to persist stream position.", ex);
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (this.raf != null) {
            this.raf.close();
        }
    }

}
