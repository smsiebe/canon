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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.geoint.canon.event.AppendedEventMessage;

/**
 *
 * @author steve_siebert
 */
public class MockAppendedEventMessage implements AppendedEventMessage {

    private final String channelName;
    private final String streamName;
    private final String eventType;
    private final String sequence;

    public MockAppendedEventMessage(String channelName, String streamName,
            String eventType, String sequence) {
        this.channelName = channelName;
        this.streamName = streamName;
        this.eventType = eventType;
        this.sequence = sequence;
    }

    /**
     * Generates an AppendedEventMessage with all event values the value of an
     * random UUID.
     *
     * @return AppendedEventMessage instance
     */
    public static MockAppendedEventMessage random() {
        return new MockAppendedEventMessage(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
    }

    public static MockAppendedEventMessage random(String channelName, 
            String streamName) {
        return new MockAppendedEventMessage(channelName, streamName,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
    }

    public static MockAppendedEventMessage random(String channelName,
            String streamName, String eventType) {
        return new MockAppendedEventMessage(channelName, streamName,
                eventType, UUID.randomUUID().toString());
    }

    @Override
    public String getSequence() {
        return sequence;
    }

    @Override
    public int getEventLength() {
        return 0;
    }

    @Override
    public String getChannelName() {
        return channelName;
    }

    @Override
    public String getStreamName() {
        return streamName;
    }

    @Override
    public String getAuthorizerId() {
        return "";
    }

    @Override
    public String[] getTriggerIds() {
        return new String[0];
    }

    @Override
    public String getEventType() {
        return this.eventType;
    }

    @Override
    public Map<String, String> getHeaders() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public Optional<String> findHeader(String headerName) {
        return Optional.empty();
    }

    @Override
    public String getHeader(String headerName, Supplier<String> defaultValue) {
        return defaultValue.get();
    }

    @Override
    public InputStream getEventContent() {
        return new ByteArrayInputStream(new byte[0]);
    }

}
