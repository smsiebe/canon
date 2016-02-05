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
package org.geoint.canon.guide;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.geoint.canon.event.EventMessage;
import org.geoint.guide.GUIDE;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author steve_siebert
 */
public class GuideIdGeneratorTest {

    private static final int VALID_PREFIX = 123;
    private static final int INVALID_PREFIX = -10;

    @Test
    public void testGenerateFromEventMessagePrefix() throws Exception {
        final EventMessage msg = messageWithPrefix(VALID_PREFIX);
        final Map<String, String> props = mapWithoutPrefix();

        GuideIdGenerator generator = new GuideIdGenerator();
        String id = generator.generate(msg, props);
        assertTrue(Objects.nonNull(id));

        GUIDE guide = GUIDE.valueOf(id);
        assertEquals(VALID_PREFIX, guide.getPrefix());
    }

    @Test
    public void testGenerateFromEventChannelPrefix() throws Exception {
        final EventMessage msg = messageWithoutPrefix();
        final Map<String, String> props = mapWithPrefix(VALID_PREFIX);

        GuideIdGenerator generator = new GuideIdGenerator();
        String id = generator.generate(msg, props);
        assertTrue(Objects.nonNull(id));

        GUIDE guide = GUIDE.valueOf(id);
        assertEquals(VALID_PREFIX, guide.getPrefix());
    }

    @Test
    public void testGenerateFromJvmPrefix() throws Exception {
        final EventMessage msg = messageWithoutPrefix();
        final Map<String, String> props = mapWithoutPrefix();
        try {
            System.setProperty(GuideIdGenerator.PREFIX_NAME,
                    String.valueOf(VALID_PREFIX));

            GuideIdGenerator generator = new GuideIdGenerator();
            String id = generator.generate(msg, props);
            assertTrue(Objects.nonNull(id));

            GUIDE guide = GUIDE.valueOf(id);
            assertEquals(VALID_PREFIX, guide.getPrefix());
        } finally {
            //"blank" property so we don't pollute for other tests
            System.setProperty(GuideIdGenerator.PREFIX_NAME, "");
        }
    }

    @Test(expected = InvalidGeneratedGUIDEException.class)
    public void testInvalidPrefix() {
        final EventMessage msg = messageWithPrefix(INVALID_PREFIX);
        final Map<String, String> props = mapWithoutPrefix();

        GuideIdGenerator generator = new GuideIdGenerator();
        String id = generator.generate(msg, props); //throws exception
    }

    @Test(expected = NoGUIDEPrefixFoundException.class)
    public void testNoPrefix() {
        final EventMessage msg = messageWithoutPrefix();
        final Map<String, String> props = mapWithoutPrefix();

        GuideIdGenerator generator = new GuideIdGenerator();
        String id = generator.generate(msg, props); //throws exception
    }

    private EventMessage messageWithPrefix(int prefix) {
        return new MockMessage(mapWithPrefix(prefix));
    }

    private EventMessage messageWithoutPrefix() {
        return new MockMessage(mapWithoutPrefix());
    }

    private Map<String, String> mapWithPrefix(int prefix) {
        Map<String, String> map = new HashMap<>(1);
        map.put(GuideIdGenerator.PREFIX_NAME, String.valueOf(prefix));
        return map;
    }

    private Map<String, String> mapWithoutPrefix() {
        return Collections.EMPTY_MAP;
    }

    private class MockMessage implements EventMessage {

        private final Map<String, String> headers;

        private MockMessage(Map<String, String> headers) {
            this.headers = headers;
        }

        @Override
        public String getChannelName() {
            return "MockMessageChannel";
        }

        @Override
        public String getStreamName() {
            return "MockStream";
        }

        @Override
        public String getAuthorizerId() {
            throw new UnsupportedOperationException("Not supported in "
                    + "MockMessage.");
        }

        @Override
        public String[] getTriggerIds() {
            throw new UnsupportedOperationException("Not supported in "
                    + "MockMessage.");
        }

        @Override
        public String getEventType() {
            return "MockEventType";
        }

        @Override
        public Map<String, String> getHeaders() {
            return headers;
        }

        @Override
        public Optional<String> findHeader(String headerName) {
            return Optional.ofNullable(headers.get(headerName));
        }

        @Override
        public String getHeader(String headerName,
                Supplier<String> defaultValue) {
            return findHeader(headerName).orElseGet(defaultValue);
        }

        @Override
        public InputStream getEventContent() {
            throw new UnsupportedOperationException("Not supported in "
                    + "MockMessage.");
        }

    }
}
