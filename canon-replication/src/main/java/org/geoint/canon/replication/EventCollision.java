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
package org.geoint.canon.replication;

import java.util.Objects;
import org.geoint.canon.event.CommittedEventMessage;
import org.geoint.canon.event.EventMessage;

/**
 * Published when two committed events have been deemed a collision and must be
 * reconciled.
 *
 * @author steve_siebert
 */
public final class EventCollision {

    private final String streamName;
    private final CommittedEventMessage first;
    private final CommittedEventMessage second;

    public EventCollision(String streamName, CommittedEventMessage first,
            CommittedEventMessage second) {
        this.streamName = streamName;
        this.first = first;
        this.second = second;
    }

    public String getStreamName() {
        return streamName;
    }

    public EventMessage getFirst() {
        return first;
    }

    public EventMessage getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return String.format("Event collision on stream '%s' between "
                + "events '%s' and '%s'",
                streamName, first.getId(), second.getId());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.streamName);
        hash = 61 * hash + Objects.hashCode(this.first);
        hash = 61 * hash + Objects.hashCode(this.second);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EventCollision other = (EventCollision) obj;
        if (!Objects.equals(this.streamName, other.streamName)) {
            return false;
        }
        if (!Objects.equals(this.first, other.first)) {
            return false;
        }
        if (!Objects.equals(this.second, other.second)) {
            return false;
        }
        return true;
    }

}
