package org.geoint.canon.impl.codec;

import java.io.IOException;
import java.io.OutputStream;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.codec.EventCodecException;
import org.geoint.canon.event.EventMessage;

/**
 * Codec used with {@link MockEncodableEvent} for codec testing.
 *
 * @see MockEncodableEvent
 * @author steve_siebert
 */
public class MockEncodableEventEncoder implements EventCodec<MockEncodableEvent> {

    @Override
    public String getSupportedEventType() {
        return "mock";
    }

    @Override
    public void encode(MockEncodableEvent event, OutputStream out)
            throws IOException, EventCodecException {

    }

    @Override
    public MockEncodableEvent decode(EventMessage e)
            throws IOException, EventCodecException {

    }

}
