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
package org.geoint.canon.tx;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.geoint.canon.event.CommittedEventMessage;

/**
 * Event created upon successful transaction commit.
 *
 * @author steve_siebert
 */
public final class TransactionCommitted extends TransactionResult {

    private final Collection<CommittedEventMessage> messages;
    private final String lastEventId;

    public TransactionCommitted(String streamName, String transactionId,
            ZonedDateTime commitTime, CommittedEventMessage... messages) {
        super(streamName, transactionId, commitTime);
        this.messages = new ArrayList<>(messages.length);

        final Collection<String> previousEventIds = new ArrayList<>(messages.length);
        final Collection<String> eventIds = new ArrayList<>(messages.length);
        Arrays.stream(messages).forEach((m) -> {
            previousEventIds.add(m.getPreviousEventId());
            eventIds.add(m.getId());
            this.messages.add(m);
        });
        eventIds.removeAll(previousEventIds); //eliminate any events that have another listed as its successor

        assert eventIds.size() == 1 : "Unable to determine the last event id "
                + "for the transactions, there is more than one event without "
                + "a successor.";

        this.lastEventId = eventIds.iterator().next();
    }

    public Collection<CommittedEventMessage> getMessages() {
        return messages;
    }

    /**
     * Returns the last event id from the committed messages.
     *
     * @return the last event id from this transaction
     */
    public String getLastEventId() {
        return lastEventId;
    }

    @Override
    public String toString() {
        return String.format("Transaction '%s' was successfully committed at "
                + "%s on stream '%s'", getTransactionId(),
                getCommitTime().format(DateTimeFormatter.ISO_DATE_TIME),
                getStreamName());
    }

}
