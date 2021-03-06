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
package org.geoint.canon.stream.event;

import java.io.Serializable;
import java.util.Objects;

/**
 * Event published when a new stream is created.
 *
 * @author steve_siebert
 */
public class StreamCreated implements Serializable {

    private final long serializableVersionUID = 1L;

    private final String channelName;
    private final String streamName;

    public StreamCreated(String channelName, String streamName) {
        this.channelName = channelName;
        this.streamName = streamName;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getStreamName() {
        return streamName;
    }

    @Override
    public String toString() {
        return String.format("Stream '%s' was created on channel '%s'.",
                streamName, channelName);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.channelName);
        hash = 89 * hash + Objects.hashCode(this.streamName);
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
        final StreamCreated other = (StreamCreated) obj;
        if (!Objects.equals(this.channelName, other.channelName)) {
            return false;
        }
        if (!Objects.equals(this.streamName, other.streamName)) {
            return false;
        }
        return true;
    }

}
