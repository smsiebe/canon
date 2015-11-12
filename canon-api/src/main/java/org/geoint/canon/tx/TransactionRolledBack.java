package org.geoint.canon.tx;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import org.geoint.canon.event.EventMessage;

/**
 * Event created upon transaction rollback.
 *
 * @author steve_siebert
 */
public class TransactionRolledBack {

    private final String streamName;
    private final String transactionId;
    private final Collection<EventMessage> messages;

    public TransactionRolledBack(String streamName, String transactionId,
            EventMessage... messages) {
        this.streamName = streamName;
        this.transactionId = transactionId;
        this.messages = Arrays.asList(messages);
    }

    public String getStreamName() {
        return streamName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Collection<EventMessage> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return String.format("Transaction '%s' rolled back, %d events were not "
                + "committed.", transactionId, messages.size());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.streamName);
        hash = 71 * hash + Objects.hashCode(this.transactionId);
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
        final TransactionRolledBack other = (TransactionRolledBack) obj;
        if (!Objects.equals(this.streamName, other.streamName)) {
            return false;
        }
        if (!Objects.equals(this.transactionId, other.transactionId)) {
            return false;
        }
        return true;
    }

}
