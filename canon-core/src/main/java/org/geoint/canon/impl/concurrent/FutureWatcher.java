package org.geoint.canon.impl.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Watches Future instances and notifies callback(s) based on its result.
 * <p>
 * Implemented as an enum to use/abuse language construct to ensure only one
 * instance per JVM.
 *
 * @author steve_siebert
 */
public enum FutureWatcher {

    INSTANCE;

    private final Collection<FutureCallback> callbacks
            = Collections.synchronizedCollection(new ArrayList<>());

    private final Thread futureMonitorThread;
    private final Executor callbackExecutor; //used to execute callbacks asynchronously
    private static final String PROPERTY_SLEEP_TIMEOUT = "canon.futureWatcher.timeout";
    private static final String DEFAULT_SLEEP_TIMEOUT = "1";
    private static final String PROPERTY_CALLBACK_THREADS = "futureWatcher.maxThreads";

    private FutureWatcher() {

        this.callbackExecutor = createCallbackExecutor();

        futureMonitorThread = new Thread(() -> {

            long timeout;
            try {
                timeout = Long.valueOf(System.getProperty(
                        PROPERTY_SLEEP_TIMEOUT, DEFAULT_SLEEP_TIMEOUT));
            } catch (NumberFormatException ex) {
                timeout = Long.valueOf(DEFAULT_SLEEP_TIMEOUT);
            }

            for (;;) {
                checkFutures();
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException ex) {
                    //keep on watching!
                }
            }
        });
        futureMonitorThread.setDaemon(true);
        futureMonitorThread.setName("Canon FutureWatcher monitor");
        futureMonitorThread.start();

    }

    public <R> void onSuccess(Future<R> future, Consumer<Future<R>> callback) {
        callbacks.add(new FutureCallback(future, callback, CallbackTrigger.COMPLETE));
    }

    public <R> void onException(Future<R> future, Consumer<Future<R>> callback) {
        callbacks.add(new FutureCallback(future, callback, CallbackTrigger.FAILED));
    }

    public <R> void onCancel(Future<R> future, Consumer<Future<R>> callback) {
        callbacks.add(new FutureCallback(future, callback, CallbackTrigger.CANCELED));
    }

    /**
     * Check futures to see if they are completed; call the appropriate
     * callbacks as needed.
     */
    private void checkFutures() {
        Iterator<FutureCallback> iterator = callbacks.iterator();
        while (iterator.hasNext()) {
            FutureCallback callback = iterator.next();
            System.out.println("checking future");
            if (callback.isDone()) {
                System.out.println("future is done");
                callbackExecutor.execute(callback::callback);
                iterator.remove();//future is complete, remove callback
            } else {
                System.out.println("future is not done");
            } 
            

        }
    }

    private Executor createCallbackExecutor() {
        try {
            int numThreads = Integer.valueOf(System.getProperty(PROPERTY_CALLBACK_THREADS));
            return Executors.newFixedThreadPool(numThreads);
        } catch (NumberFormatException ex) {
            return Executors.newCachedThreadPool();
        }
    }

    enum CallbackTrigger {
        COMPLETE, FAILED, CANCELED
    };

    /**
     * Encapsulates the individual future callback.
     *
     * @param <R>
     */
    private class FutureCallback<R> {

        private final Future<R> future;
        private final Consumer<Future<R>> callback;
        private final CallbackTrigger trigger;

        public FutureCallback(Future<R> future, Consumer<Future<R>> callback,
                CallbackTrigger trigger) {
            this.future = future;
            this.callback = callback;
            this.trigger = trigger;
        }

        /**
         * Returns true if the callback has completed, was canceled, or an
         * exception was thrown
         *
         * @return
         */
        public boolean isDone() {
            return future.isDone();
        }

        public void callback() {
            if (!future.isDone()) {
                System.out.println("future is not done!");
                throw new IllegalStateException("Asynchronous operation has not "
                        + "completed yet, unable to exectute callback.");
            }
            if (isCorrectState()) {
                System.out.println("correct state");
                callback.accept(future);
            }
        }

        /**
         * Checks if the result/state of the future is relevant for this
         * callback registration.
         *
         * @return true if this callback should be executed, otherwise false
         */
        private boolean isCorrectState() {
            
            if (trigger.equals(CallbackTrigger.CANCELED)) {
                System.out.println("Cancelled!");
            } else {
                System.out.println("nope, trigger type is "+trigger.name());
            }
            if (future.isCancelled() && trigger.equals(CallbackTrigger.CANCELED)) {
                return true;
            }

            try {
                future.get();
                if (trigger.equals(CallbackTrigger.COMPLETE)) {
                    return true;
                }
            } catch (InterruptedException ex) {
                assert false : "Future callback was interrupted but the results "
                        + "should have already been completed.";
            } catch (ExecutionException ex) {
                if (trigger.equals(CallbackTrigger.FAILED)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + Objects.hashCode(this.future);
            hash = 17 * hash + Objects.hashCode(this.callback);
            hash = 17 * hash + Objects.hashCode(this.trigger);
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
            final FutureCallback<?> other = (FutureCallback<?>) obj;
            if (!Objects.equals(this.future, other.future)) {
                return false;
            }
            if (!Objects.equals(this.callback, other.callback)) {
                return false;
            }
            return this.trigger == other.trigger;
        }

    }
}
