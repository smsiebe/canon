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
package org.geoint.canon.stream.event;

import java.io.Serializable;
import java.util.Objects;

/**
 * Canon Event Channel was created.
 *
 * @author steve_siebert
 */
public final class ChannelCreated implements Serializable {

    private final long serializableVersionUID = 1L;
    private final String channelName;

    public ChannelCreated(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelName() {
        return channelName;
    }

    @Override
    public String toString() {
        return String.format("Event channel '%s' was created.", channelName);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.channelName);
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
        final ChannelCreated other = (ChannelCreated) obj;
        if (!Objects.equals(this.channelName, other.channelName)) {
            return false;
        }
        return true;
    }

}
