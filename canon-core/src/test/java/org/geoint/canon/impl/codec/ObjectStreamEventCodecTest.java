/*
 * Copyright 2016 geoint.org.
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
package org.geoint.canon.impl.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.geoint.canon.codec.EventCodecException;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.stream.event.StreamCreated;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author steve_siebert
 */
public class ObjectStreamEventCodecTest {

    @Test
    public void testIsSupportedSerializable() {
        ObjectStreamEventCodec codec = new ObjectStreamEventCodec();
        assertTrue(codec.isSupported(StreamCreated.class.getCanonicalName()));
    }

    @Test
    public void testIsSupportedExternalizable() {
        ObjectStreamEventCodec codec = new ObjectStreamEventCodec();
        assertTrue(codec.isSupported(MockExternalizableEvent.class.getName()));
    }

    @Test
    public void testIsSupportNonSerializable() {
        ObjectStreamEventCodec codec = new ObjectStreamEventCodec();
        assertFalse(codec.isSupported(ObjectStreamEventCodecTest.class.getCanonicalName()));
    }

    @Test
    public void testEncodeSerializable() throws Exception {
        ObjectStreamEventCodec codec = new ObjectStreamEventCodec();

        StreamCreated created = new StreamCreated("testChannel", "testStream");
        byte[] bytes;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            codec.encode(created, out);
            bytes = out.toByteArray();
        }
        assertTrue(bytes.length > 0);
    }

    @Test
    public void testEncodeExternalizable() throws Exception {
        ObjectStreamEventCodec codec = new ObjectStreamEventCodec();

        MockExternalizableEvent created = new MockExternalizableEvent("testChannel");
        byte[] bytes;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            codec.encode(created, out);
            bytes = out.toByteArray();
        }
        assertTrue(bytes.length > 0);
    }

    @Test(expected = EventCodecException.class)
    public void testEncodeNonSerializable() throws Exception {
        ObjectStreamEventCodec codec = new ObjectStreamEventCodec();

        byte[] bytes;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            codec.encode(codec, out);
            bytes = out.toByteArray();
        }
        assertTrue(bytes.length > 0);
    }

    @Test
    public void testDecodeSerializable() throws Exception {
        ObjectStreamEventCodec codec = new ObjectStreamEventCodec();

        StreamCreated created = new StreamCreated("testChannel", "testStream");
        byte[] bytes;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            codec.encode(created, out);
            bytes = out.toByteArray();
        }

        EventMessage msg = new SerializaedObjectEventMessage(StreamCreated.class, bytes);
        StreamCreated decoded = (StreamCreated) codec.decode(msg);
        assertEquals(created, decoded);
    }

    @Test
    public void testDecodeExternalizable() throws Exception {
        ObjectStreamEventCodec codec = new ObjectStreamEventCodec();

        MockExternalizableEvent created = new MockExternalizableEvent("testChannel");
        byte[] bytes;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            codec.encode(created, out);
            bytes = out.toByteArray();
        }
        assertTrue(bytes.length > 0);

        EventMessage msg = new SerializaedObjectEventMessage(MockExternalizableEvent.class, bytes);
        MockExternalizableEvent decoded = (MockExternalizableEvent) codec.decode(msg);
        assertEquals(created, decoded);
    }

    private class SerializaedObjectEventMessage implements EventMessage {

        private final String eventType;
        private final byte[] content;

        public SerializaedObjectEventMessage(Class eventClass, byte[] content) {
            this.eventType = eventClass.getName();
            this.content = content;
        }

        @Override
        public String getChannelName() {
            return "testChannel";
        }

        @Override
        public String getStreamName() {
            return "testStream";
        }

        @Override
        public String getAuthorizerId() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String[] getTriggerIds() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getEventType() {
            return eventType;
        }

        @Override
        public Map<String, String> getHeaders() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Optional<String> findHeader(String headerName) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getHeader(String headerName,
                Supplier<String> defaultValue) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public InputStream getEventContent() {
            return new ByteArrayInputStream(content);
        }

    }

    public static class MockExternalizableEvent implements Externalizable {

        private final long serialVersionUID = 1L;
        private String myString;

        public MockExternalizableEvent() {
        }

        public MockExternalizableEvent(String myString) {
            this.myString = myString;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeUTF(myString);
        }

        @Override
        public void readExternal(ObjectInput in)
                throws IOException, ClassNotFoundException {
            myString = in.readUTF();
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 19 * hash + Objects.hashCode(this.myString);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final MockExternalizableEvent other = (MockExternalizableEvent) obj;
            if (!Objects.equals(this.myString, other.myString)) {
                return false;
            }
            return true;
        }

    }
}
