package org.geoint.canon.stream;

import org.geoint.canon.stream.event.EventsAppended;
import java.util.concurrent.Future;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.event.EventMessageBuilder;

/**
 * Simple event appender decorator which delegates all events to the decorated
 * appender.
 *
 * @author steve_siebert
 */
public abstract class EventAppenderDecorator implements EventAppender {

    protected final EventAppender appender;

    public EventAppenderDecorator(EventAppender appender) {
        this.appender = appender;
    }

    @Override
    public EventMessageBuilder create(String eventType) throws StreamAppendException {
        return appender.create(eventType);
    }

    @Override
    public EventAppender add(EventMessage message) throws StreamAppendException {
        return appender.add(message);
    }

    @Override
    public EventAppender useCodec(EventCodec codec) {
        return appender.useCodec(codec);
    }

    @Override
    public EventsAppended append() throws StreamAppendException {
        return appender.append();
    }

}
