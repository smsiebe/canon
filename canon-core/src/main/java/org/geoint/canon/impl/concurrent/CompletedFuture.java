package org.geoint.canon.impl.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Simple Future wrapper for completed results.
 *
 * @author steve_siebert
 * @param <T>
 */
public class CompletedFuture<T> implements Future<T> {

    private final T results;
    private final ExecutionException exception;

    /**
     * Create a completed future, setting the results if it wasn't canceled.
     *
     * @param results null if canceled, otherwise is done
     */
    private CompletedFuture(T results) {
        this.results = results;
        this.exception = null;
    }

    private CompletedFuture(Throwable ex) {
        this.results = null;
        this.exception = new ExecutionException(ex);
    }

    /**
     * Create a completed future in canceled state
     */
    private CompletedFuture() {
        this.results = null;
        this.exception = null;
    }

    /**
     * Wraps the results of a successfully completed operation.
     *
     * @param <T>
     * @param results
     * @return
     */
    public static <T> CompletedFuture<T> completed(T results) {
        return new CompletedFuture(results);
    }

    /**
     * Wraps the results of a canceled operation.
     *
     * @return
     */
    public static CompletedFuture canceled() {
        return new CompletedFuture();
    }

    /**
     * Wraps the results of an operation that threw an exception.
     *
     * @param ex
     * @return
     */
    public static CompletedFuture exception(Throwable ex) {
        return new CompletedFuture(ex);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return true;
    }

    @Override
    public boolean isCancelled() {
        return exception == null && results == null;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        if (exception != null) {
            throw exception;
        }
        if (results == null) {
            throw new CancellationException();
        }
        return results;
    }

    @Override
    public T get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return get();
    }

}
