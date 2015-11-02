/*
 * Copyright 2015 Steve Siebert <steve@t-3-solutions.com>.
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
package org.geoint.canon;

import org.geoint.canon.EventException;

/**
 *
 * @author Steve Siebert <steve@t-3-solutions.com>
 */
public class StreamAlreadyExistsException extends EventException {

    private final String channelName;
    private final String streamName;

    public StreamAlreadyExistsException(String channelName, String streamName) {
        super(message(channelName, streamName));
        this.channelName = channelName;
        this.streamName = streamName;
    }

    public StreamAlreadyExistsException(String channelName, String streamName, Throwable cause) {
        super(message(channelName, streamName), cause);
        this.channelName = channelName;
        this.streamName = streamName;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getStreamName() {
        return streamName;
    }

    private static String message(String channelName, String streamName) {
        return String.format("Stream '%s' already exists on channel '%s'.",
                streamName, channelName);
    }
}
