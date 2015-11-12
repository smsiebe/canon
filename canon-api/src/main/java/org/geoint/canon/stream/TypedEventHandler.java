package org.geoint.canon.stream;

import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.event.CommittedEventMessage;

/**
 * An event handler which only notifies on a single event type.
 *
 * @author steve_siebert
 * @param <T>
 */
public abstract class TypedEventHandler<T> implements EventHandler {

    private final EventCodec<T> codec;

    public TypedEventHandler(EventCodec<T> codec) {
        this.codec = codec;
    }

    @Override
    public void handle(CommittedEventMessage event) throws Exception {
        if (!event.getEventType().equalsIgnoreCase(codec.getSupportedEventType())) {
            return;
        }
        
        handle(event, codec.decode(event));
    }

    protected abstract void handle(CommittedEventMessage msg,
            T event);
}
