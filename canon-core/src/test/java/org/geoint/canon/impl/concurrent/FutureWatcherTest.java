package org.geoint.canon.impl.concurrent;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Tests the FutureWatcher.
 *
 *
 * @author steve_siebert
 */
public class FutureWatcherTest {

    //create only a single instance, since this is a JVM singleton 
    private FutureWatcher watcher;

    @Before
    public void before() {
        watcher = FutureWatcher.INSTANCE;
    }

    /**
     * Test the future watcher calls only the onSuccess callback for a
     * successfully completed Future.
     */
    @Test
    public void testOnSuccess() throws Throwable {
        CompletedFuture<Object> success
                = CompletedFuture.completed(new Object());

        AtomicBoolean isCalled = new AtomicBoolean(false);
        watcher.onSuccess(success, (f) -> isCalled.set(true));
        Thread.sleep(10); //give the watcher some time to process
        assertTrue("success callback was not called", isCalled.get());

        watcher.onCancel(success, (f) -> fail("successful test called cancel"
                + " callback"));

        watcher.onException(success, (f) -> fail("successful test called"
                + " exception callback"));
    }

    @Test
    public void testOnException() throws Throwable {

        CompletedFuture<Object> exception
                = CompletedFuture.exception(
                        new RuntimeException("testing exception callback")
                );

        watcher.onSuccess(exception, (f) -> fail("exception test called success "
                + "callback"));

        watcher.onCancel(exception, (f) -> fail("exception test called cancel "
                + "callback"));

        AtomicBoolean isCalled = new AtomicBoolean(false);
        watcher.onException(exception, (f) -> isCalled.set(true));
        Thread.sleep(10); //give the watcher some time to process
        assertTrue("exception callback was not called", isCalled.get());
    }

    @Test
    public void testOnCancel() throws Throwable {
        CompletedFuture<Object> canceled
                = CompletedFuture.canceled();

        watcher.onSuccess(canceled, (f) -> fail("cancel test called success "
                + "callback"));

        AtomicBoolean isCalled = new AtomicBoolean(false);
        watcher.onCancel(canceled, (f) -> isCalled.set(true));
        Thread.sleep(10); //give the watcher some time to process
        assertTrue("cancel callback was not called", isCalled.get());

        watcher.onException(canceled, (f) -> fail("cancel test called "
                + "exception callback"));
    }

}
