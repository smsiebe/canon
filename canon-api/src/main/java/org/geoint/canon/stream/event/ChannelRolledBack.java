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

/**
 * Published to the channel administrative stream when the channel must be
 * rolled back to a specified event.
 * <p>
 * This event will cause canon to rollback the channel, and all streams, to the
 * specified point. Downstream systems will receive this event any time an event
 * is requested past this point.
 *
 * @author steve_siebert
 */
public final class ChannelRolledBack {

    private final String channelName;
    private final String rollbackEventId;
    private final String justification;

    public ChannelRolledBack(String channelName, String rollbackEventId,
            String justification) {
        this.channelName = channelName;
        this.rollbackEventId = rollbackEventId;
        this.justification = justification;
    }

    public String getJustification() {
        return justification;
    }

    public String getRollbackEventId() {
        return rollbackEventId;
    }

    public String getChannelName() {
        return channelName;
    }

    @Override
    public String toString() {
        return String.format("Event channel '%s' was rolled back to event '%s'.",
                channelName, rollbackEventId);
    }

}
