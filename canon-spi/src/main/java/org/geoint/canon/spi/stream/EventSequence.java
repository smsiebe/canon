package org.geoint.canon.spi.stream;

/**
 * Sequence of an event within a stream.
 * 
 * @author steve_siebert
 */
public interface EventSequence {
 
    /**
     * Format the sequence as a unique string.
     * 
     * @return sequence formatted as a string
     */
    String asString();
}
