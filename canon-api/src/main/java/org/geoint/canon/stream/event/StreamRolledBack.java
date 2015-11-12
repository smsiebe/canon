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

import java.util.Objects;

/**
 * Event detailing a stream has become invalid and must be rolled back to a
 * specific point.
 *
 * @author steve_siebert
 */
public final class StreamRolledBack {

    private final String streamName;
    private final String lastEventId;
    private final String justification;

    public StreamRolledBack(String streamName, String lastEventId,
            String justification) {
        this.streamName = streamName;
        this.lastEventId = lastEventId;
        this.justification = justification;
    }

    public String getStreamName() {
        return streamName;
    }

    public String getLastEventId() {
        return lastEventId;
    }

    public String getJustification() {
        return justification;
    }

    @Override
    public String toString() {
        return String.format("Event stream '%s' was rolled back to event '%s'.",
                streamName, lastEventId);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.streamName);
        hash = 89 * hash + Objects.hashCode(this.lastEventId);
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
        final StreamRolledBack other = (StreamRolledBack) obj;
        if (!Objects.equals(this.streamName, other.streamName)) {
            return false;
        }
        if (!Objects.equals(this.lastEventId, other.lastEventId)) {
            return false;
        }
        return true;
    }

}
