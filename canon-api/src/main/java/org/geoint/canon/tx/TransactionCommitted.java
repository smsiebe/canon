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
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import org.geoint.canon.event.CommittedEventMessage;
import org.geoint.canon.event.EventMessage;

/**
 * Event created upon successful transaction commit.
 *
 * @author steve_siebert
 */
public final class TransactionCommitted {

    private final String streamName;
    private final String transactionId;
    private final ZonedDateTime commitTime;
    private final Collection<CommittedEventMessage> messages;

    public TransactionCommitted(String streamName, String transactionId,
            ZonedDateTime commitTime, CommittedEventMessage... messages) {
        this.streamName = streamName;
        this.transactionId = transactionId;
        this.commitTime = commitTime;
        this.messages = Arrays.asList(messages);
    }

    public String getStreamName() {
        return streamName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public ZonedDateTime getCommitTime() {
        return commitTime;
    }

    public Collection<CommittedEventMessage> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return String.format("Transaction '%s' was successfully committed at "
                + "%s on stream '%s'", transactionId,
                commitTime.format(DateTimeFormatter.ISO_DATE_TIME),
                streamName);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.streamName);
        hash = 29 * hash + Objects.hashCode(this.transactionId);
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
        final TransactionCommitted other = (TransactionCommitted) obj;
        if (!Objects.equals(this.streamName, other.streamName)) {
            return false;
        }
        return Objects.equals(this.transactionId, other.transactionId);
    }

}
