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
package org.geoint.canon.stream.mock;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.geoint.canon.codec.CodecResolver;
import org.geoint.canon.impl.stream.AbstractEventChannel;
import org.geoint.canon.spi.stream.EventChannelProvider;
import org.geoint.canon.spi.stream.UnableToResolveChannelException;
import org.geoint.canon.stream.ChannelInitializationException;
import org.geoint.canon.stream.EventChannel;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.StreamAlreadyExistsException;

/**
 * Mock channel for test purposes only.
 * <p>
 * Implementation is not thread-safe.
 *
 * @author steve_siebert
 */
public class MockChannelProvider implements EventChannelProvider {

    public static final String SCHEME = "mock";

    @Override
    public boolean provides(String scheme, Map<String, String> channelProperties) {
        return scheme.equalsIgnoreCase(SCHEME);
    }

    @Override
    public EventChannel getChannel(String channelName,
            Map<String, String> channelProperties, CodecResolver codecs)
            throws UnableToResolveChannelException, ChannelInitializationException {
        return new MockEventChannel(channelName, channelProperties, codecs);
    }

    private class MockEventChannel extends AbstractEventChannel {

        public MockEventChannel(String name,
                Map<String, String> channelProperties,
                CodecResolver codecs) throws ChannelInitializationException {
            super(name, channelProperties, codecs);
        }

        @Override
        public Collection<EventStream> listStreams() {
            throw new UnsupportedOperationException("Not supported by "
                    + "MockEventChannel.");
        }

        @Override
        public Optional<EventStream> findStream(String streamName) {
            throw new UnsupportedOperationException("Not supported by "
                    + "MockEventChannel.");
        }

        @Override
        protected EventStream createStream(String streamName, CodecResolver codecs)
                throws StreamAlreadyExistsException {
            throw new UnsupportedOperationException("Not supported by "
                    + "MockEventChannel.");
        }

        @Override
        public void close() throws IOException {
            //nothing to do
        }

    }
}
