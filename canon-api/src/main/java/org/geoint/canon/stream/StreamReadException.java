/*
 * Copyright 2015 geoint.org.
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
package org.geoint.canon.stream;

import org.geoint.canon.event.EventException;

/**
 * Thrown if there is a problem reading from an event stream.
 *
 * @author steve_siebert
 */
public class StreamReadException extends EventException {

    private final String channelName;
    private final String streamName;

    public StreamReadException(String channelName, String streamName, String message) {
        super(prefixMessage(channelName, streamName, message));
        this.channelName = channelName;
        this.streamName = streamName;
    }

    public StreamReadException(String channelName, String streamName, String message, Throwable cause) {
        super(prefixMessage(channelName, streamName, message), cause);
        this.channelName = channelName;
        this.streamName = streamName;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getStreamName() {
        return streamName;
    }

    private static String prefixMessage(String channelName, String streamName,
            String message) {
        return String.format("Problems reading events from stream %s-%s: %s",
                channelName, streamName, message);
    }
}
