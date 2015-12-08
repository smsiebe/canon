package org.geoint.canon.impl.stream;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.geoint.canon.event.AppendedEventMessage;
import org.geoint.canon.event.UnknownEventException;
import org.geoint.canon.stream.EventReader;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.StreamReadException;

/**
 *
 * @author steve_siebert
 */
public class AbstractEventReader implements EventReader {

    @Override
    public boolean hasNext() {

    }

    @Override
    public Optional<AppendedEventMessage> poll() throws StreamReadException {

    }

    @Override
    public Optional<AppendedEventMessage> poll(long timeout, TimeUnit unit) throws StreamReadException, InterruptedException {

    }

    @Override
    public AppendedEventMessage take() throws StreamReadException, TimeoutException, InterruptedException {

    }

    @Override
    public void setPosition(String sequence) throws UnknownEventException {

    }

    @Override
    public EventStream getStream() {

    }

    @Override
    public void close() throws IOException {

    }
    
}
