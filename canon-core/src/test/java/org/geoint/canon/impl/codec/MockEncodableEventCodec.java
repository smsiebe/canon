package org.geoint.canon.impl.codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.codec.EventCodecException;
import org.geoint.canon.event.EventMessage;
import static org.geoint.canon.impl.codec.MockEncodableEvent.EVENT_TYPE;

/**
 * Codec used with {@link MockEncodableEvent} for codec testing.
 *
 * @see MockEncodableEvent
 * @author steve_siebert
 */
public class MockEncodableEventCodec implements EventCodec<MockEncodableEvent> {

    @Override
    public boolean isSupported(String eventType) {
        return eventType.equalsIgnoreCase(EVENT_TYPE);
    }

    @Override
    public void encode(MockEncodableEvent event, OutputStream out)
            throws IOException, EventCodecException {
        DataOutputStream dout = new DataOutputStream(out);
        dout.writeInt(event.getMyInteger());
    }

    @Override
    public MockEncodableEvent decode(EventMessage e)
            throws IOException, EventCodecException {
        DataInputStream din = new DataInputStream(e.getEventContent());
        return new MockEncodableEvent(din.readInt());
    }

}
