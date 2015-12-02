package org.geoint.canon.stream;

import java.util.Optional;
import java.util.function.Predicate;
import org.geoint.canon.event.AppendedEventMessage;
import org.geoint.canon.event.EventMessage;

/**
 * Applies a filter to a stream of events read from an event reader.
 *
 * @author steve_siebert
 */
public class FilteredEventReader extends EventReaderDecorator {

    private final Predicate<EventMessage> filter;

    public FilteredEventReader(EventReader reader,
            Predicate<EventMessage> filter) {
        super(reader);
        this.filter = filter;
    }

    @Override
    public Optional<AppendedEventMessage> poll() throws StreamReadException {
        return reader.poll().filter(filter);
    }

}
