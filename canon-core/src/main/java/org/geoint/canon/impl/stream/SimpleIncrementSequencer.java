package org.geoint.canon.impl.stream;

import org.geoint.canon.spi.stream.EventSequencer;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.stream.StreamAppendException;

/**
 * A very simple "one-up" increment sequencer.
 * <p>
 * This is a simple/naive implementation which can in some regards be considered
 * a test-only/toy implementation, but could conceivably be used in distributed
 * production, given the reconciliation behavior of Canon. Depending on how an
 * application uses event streaming, this sequencer may be adequate or may
 * create a lot of perhaps unnecessary collision mediation.
 * <p>
 * In this implementation, if an appender fails to append there will be loss of
 * those increments; this has no functional impact.
 *
 * @author steve_siebert
 */
public class SimpleIncrementSequencer implements EventSequencer {

    private final AtomicLong increment;

    public SimpleIncrementSequencer() {
        increment = new AtomicLong(0L); //being explict is nice
    }

    public SimpleIncrementSequencer(long increment) {
        this.increment = new AtomicLong(increment);
    }

    @Override
    public String next(EventMessage msg) throws StreamAppendException {
        return String.valueOf(increment.incrementAndGet());
    }

    @Override
    public Comparator<String> getComparator() {
        return (s1, s2) -> Long.valueOf(s1).compareTo(Long.valueOf(s2));
    }

}
