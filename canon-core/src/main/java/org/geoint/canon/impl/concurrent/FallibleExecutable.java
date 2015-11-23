package org.geoint.canon.impl.concurrent;

/**
 * An operation that can throw an exception if it fails.
 *
 * @author steve_siebert
 */
@FunctionalInterface
public interface FallibleExecutable {

    /**
     * Executable operation.
     * 
     * @throws Throwable 
     */
    void execute() throws Throwable;
}
