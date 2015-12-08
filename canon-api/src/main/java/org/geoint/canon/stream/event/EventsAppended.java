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

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import org.geoint.canon.event.AppendedEventMessage;

/**
 * Canon stream event providing details about an append operation on a stream.
 *
 * @author steve_siebert
 */
public final class EventsAppended {

    private final String streamName;
    private final ZonedDateTime appendTime;
    private final Collection<AppendedEventMessage> messages;

    public EventsAppended(String streamName, AppendedEventMessage... messages) {
        this.streamName = streamName;
        this.appendTime = ZonedDateTime.now(ZoneOffset.UTC);

        this.messages = Arrays.asList(messages);
//        this.messages = new ArrayList<>(messages.length);
//
//        final Collection<String> previousEventIds = new ArrayList<>(messages.length);
//        final Collection<String> eventIds = new ArrayList<>(messages.length);
//        Arrays.stream(messages).forEach((m) -> {
//            previousEventIds.add(m.getPreviousEventId());
//            eventIds.add(m.getId());
//            this.messages.add(m);
//        });
//        eventIds.removeAll(previousEventIds); //eliminate any events that have another listed as its successor
//
//        assert eventIds.size() == 1 : "Unable to determine the last event id "
//                + "for the transactions, there is more than one event without "
//                + "a successor.";
//
//        this.lastEventId = eventIds.iterator().next();
    }

    public Collection<AppendedEventMessage> getMessages() {
        return messages;
    }

    public String getStreamName() {
        return streamName;
    }

    public ZonedDateTime getAppendTime() {
        return appendTime;
    }

    @Override
    public String toString() {
        return String.format("%d events were successfully appended to "
                + "%s at '%s'",
                messages.size(), getStreamName(),
                appendTime.format(DateTimeFormatter.ISO_DATE_TIME)
        );
    }

}
