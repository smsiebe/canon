package org.geoint.canon.impl.stream;

import java.io.File;
import java.util.function.Predicate;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.event.AppendedEventMessage;

/**
 * An event stream which is accessed locally (on the same system as the canon
 * instance is running) allowing for local operations not relying on network
 * connectivity.
 *
 * @author steve_siebert
 */
public abstract class LocalEventStream implements EventStream {

    /**
     * Enable event archiving.
     *
     * @param archiveDir directory to contain archive streams
     * @param compress true to compress the archive file, otherwise do not
     * compress
     * @param archiveFilter filter to determine if the message should be
     * archived
     */
    public void enableArchive(File archiveDir, boolean compress,
            Predicate<AppendedEventMessage> archiveFilter) {
        throw new UnsupportedOperationException();
    }

    /**
     * Check if the stream is archiving.
     *
     * @return true if archiving, otherwise false
     */
    public boolean isArchiving() {
        throw new UnsupportedOperationException();
    }

    /**
     * If archive(s) exist, move the archived events back to the primary stream
     * directory, delete the archive and disable the archive.
     * <p>
     * This is a synchronous operation executed by the thread requesting this
     * operation.
     */
    public void deleteArchive() {
        throw new UnsupportedOperationException();
    }

}
