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
package org.geoint.canon.stream;

import org.geoint.canon.event.EventException;

/**
 * Thrown if a stream could not be created within a channel.
 *
 * @author steve_siebert
 */
public class StreamInitializationException extends EventException {

    private final String streamName;

    public StreamInitializationException(String streamName) {
        this.streamName = streamName;
    }

    public StreamInitializationException(String streamName, String message) {
        super(message);
        this.streamName = streamName;
    }

    public StreamInitializationException(String streamName, String message, Throwable cause) {
        super(message, cause);
        this.streamName = streamName;
    }

    public StreamInitializationException(String streamName, Throwable cause) {
        super(cause);
        this.streamName = streamName;
    }

    public String getStreamName() {
        return streamName;
    }

}
