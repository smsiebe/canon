package org.geoint.canon.stream.memory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.geoint.canon.event.AppendedEventMessage;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.stream.StreamAppendException;

/**
 * Stores the entire appended event message (including content of the event) on
 * heap memory.
 *
 * @author steve_siebert
 */
public class HeapAppendedEventMessage implements AppendedEventMessage {

    private final String sequence;
    private final EventMessage msg;
    private final byte[] content;

    private HeapAppendedEventMessage(String sequence,
            EventMessage msg, byte[] content) {
        this.sequence = sequence;
        this.msg = msg;
        this.content = content;
    }

    /**
     * Create a new on-heap appended event message from the provided event
     * message.
     *
     * @param sequence
     * @param msg
     * @return
     * @throws StreamAppendException
     */
    public static HeapAppendedEventMessage fromMessage(String sequence,
            EventMessage msg)
            throws StreamAppendException {
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
                InputStream in = msg.getEventContent();) {
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                bout.write(buffer, 0, read);
            }
            byte[] bytes = bout.toByteArray();
            return new HeapAppendedEventMessage(sequence, msg, bytes);
        } catch (Throwable ex) {
            throw new StreamAppendException("Unable to read event message "
                    + "content into memory.", ex);
        }
    }

    @Override
    public String getSequence() {
        return sequence;
    }

    @Override
    public int getEventLength() {
        return content.length;
    }

    @Override
    public String getChannelName() {
        return msg.getChannelName();
    }

    @Override
    public String getStreamName() {
        return msg.getStreamName();
    }

    @Override
    public String getAuthorizerId() {
        return msg.getAuthorizerId();
    }

    @Override
    public String[] getTriggerIds() {
        return msg.getTriggerIds();
    }

    @Override
    public String getEventType() {
        return msg.getEventType();
    }

    @Override
    public Map<String, String> getHeaders() {
        return msg.getHeaders();
    }

    @Override
    public Optional<String> findHeader(String headerName) {
        return msg.findHeader(headerName);
    }

    @Override
    public String getHeader(String headerName, Supplier<String> defaultValue) {
        return msg.getHeader(headerName, defaultValue);
    }

    @Override
    public InputStream getEventContent() {
        return msg.getEventContent();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.getChannelName());
        hash = 97 * hash + Objects.hashCode(this.getStreamName());
        hash = 97 * hash + Objects.hashCode(this.sequence);
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
        final HeapAppendedEventMessage other = (HeapAppendedEventMessage) obj;
        if (!this.getChannelName().contentEquals(other.getChannelName())) {
            return false;
        }
        if (!this.getStreamName().contentEquals(other.getStreamName())) {
            return false;
        }
        return Objects.equals(this.sequence, other.sequence);
    }

}
