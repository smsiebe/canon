package org.geoint.canon.impl.stream;

import java.util.concurrent.Future;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.event.EventMessageBuilder;
import org.geoint.canon.stream.EventAppender;
import org.geoint.canon.stream.StreamAppendException;
import org.geoint.canon.tx.EventTransactionException;
import org.geoint.canon.tx.TransactionCommitted;
import org.geoint.canon.tx.TransactionRolledBack;

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
    public EventAppender append(EventMessage message) throws StreamAppendException {
        return appender.append(message);
    }

    @Override
    public EventAppender useCodec(EventCodec codec) {
        return appender.useCodec(codec);
    }

    @Override
    public Future<TransactionCommitted> commit() throws EventTransactionException {
        return appender.commit();
    }

    @Override
    public Future<TransactionRolledBack> rollback() throws EventTransactionException {
        return appender.rollback();
    }

}
