/*
 * Copyright 2016 geoint.org.
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
package org.geoint.canon.async;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.geoint.canon.event.AppendedEventMessage;
import org.geoint.canon.stream.EventHandler;
import org.geoint.canon.stream.EventHandlerAction;
import org.geoint.canon.stream.EventReader;
import org.geoint.canon.stream.StreamReadException;

//TODO add JMX
/**
 * Executes {@link EventHandler#handle(AppendedEventMessage) event handlers}
 * asynchronously on an internally managed ThreadGroup.
 *
 * @author steve_siebert
 */
public class AsyncHandlerNotifier {

    public static final int DEFAULT_NUM_NOTIFIER_THREADS = 3;

    private final ThreadGroup notifierGroup;
    private final List<HandlerNotifier> notifiers;
    //not thread-safe, use the field itself to synchronize access to it
    private final List<HandlerContext> handlers = new ArrayList<>();
    /*
     * Simple index/position tracking mechanism for the workers to know which 
     * HandlerContext to execute next. This effectively creates a round-robin
     * style notification mechanism.
     *
     * TODO - implement a pluggable notification strategy as well as an 
     *        implementation that supports stream prioritization.
     */
    private final AtomicInteger workerPosition = new AtomicInteger();

    private static final String NOTIFIER_THREAD_NAME_PREFIX
            = "EventHandlerNotifier";
    private static final Logger LOGGER
            = Logger.getLogger(AsyncHandlerNotifier.class.getName());

    /**
     * Create a notifier with the {@link AsyncHandlerNotifier#DEFAULT_NUM_NOTIFIER_THREADS
     * default number of threads}.
     */
    public AsyncHandlerNotifier() {
        this(DEFAULT_NUM_NOTIFIER_THREADS);
    }

    /**
     * Create a notifier with a specified number of notification threads.
     *
     * @param numThreads number of notification threads
     */
    public AsyncHandlerNotifier(int numThreads) {
        this.notifierGroup = new ThreadGroup("AsyncHandlerNotifiers-"+UUID.randomUUID().toString());
        this.notifierGroup.setDaemon(true);

        this.notifiers = new ArrayList<>(numThreads);

        for (int i = 0; i < numThreads; i++) {
            this.addNotificationThread();
        }
    }

    public void addHandler(EventHandler handler, EventReader reader) {
        synchronized (handlers) {
            handlers.add(new HandlerContext(reader, handler));
        }
    }

    public Collection<EventHandler> listHandlers() {
        synchronized (handlers) {
            return handlers.stream()
                    .map(HandlerContext::getHandler)
                    .collect(Collectors.toList());
        }
    }

    public void removeHandler(String channelName, String streamName,
            EventHandler handler) {
        removeHandlers((hc) -> hc.getHandler().equals(handler)
                && hc.getReader().getChannelName().contentEquals(channelName)
                && hc.getReader().getStreamName().contentEquals(streamName));
    }

    /**
     * Removes all handlers associated with the specified stream.
     *
     * @param channelName channel name
     * @param streamName stream name
     */
    public void removeAllHandlers(String channelName, String streamName) {
        removeHandlers((hc) -> hc.getReader().getChannelName().contentEquals(channelName)
                && hc.getReader().getStreamName().contentEquals(streamName));
    }

    private void removeHandlers(Predicate<HandlerContext> filter) {
        synchronized (handlers) {
            Iterator<HandlerContext> iterator = handlers.iterator();
            while (iterator.hasNext()) {
                HandlerContext hc = iterator.next();
                if (filter.test(hc)) {
                    //invalidate so any worker currently with a reference to 
                    //the handler object but not yet notifying (ie waiting on 
                    //thread notify) will not execute again
                    hc.valid = false;
                    iterator.remove();
                }
            }
        }
    }

    public void setNumThreads(int numThreads) {
        int numNotifiers;
        while ((numNotifiers = numNotifiers()) != numThreads) {
            if (numNotifiers > numThreads) {
                addNotificationThread();
            } else {
                removeNotificationThread();
            }
        }
    }

    public void shutdown() {
        //tell the notifiers to shut down - they are daemon threads doing 
        //transactional work, so if they "die" in the middle of their handling 
        //operation, it's no big deal.
        while (numNotifiers() > 0) {
            removeNotificationThread();
        }
    }

    /**
     * Returns the number of HandlerNotifier instances that are defined to keep
     * running.
     *
     * @return number of notifiers still running
     */
    private int numNotifiers() {
        synchronized (notifiers) {
            return (int) notifiers.stream()
                    .filter(HandlerNotifier::isActiveNotifier)
                    .count();
        }
    }

    private void addNotificationThread() {
        synchronized (notifiers) {
            HandlerNotifier notifier = new HandlerNotifier();
            Thread t = new Thread(notifierGroup, notifier);
            t.setDaemon(true);
            t.setName(String.format("%s-%s", NOTIFIER_THREAD_NAME_PREFIX,
                    UUID.randomUUID().toString()));
            t.setUncaughtExceptionHandler((th, ex) -> {
                LOGGER.log(Level.WARNING, String.format("Uncaught event handler "
                        + "notifier exception running on thread '%s'",
                        th.getName()), ex);
            });
            t.start();
            notifiers.add(notifier);
        }
    }

    private void removeNotificationThread() {
        //tell a notification thread to shut itself down
        synchronized (notifiers) {
            notifiers.stream()
                    .filter(HandlerNotifier::isActiveNotifier)
                    .findFirst()
                    .ifPresent((n) -> n.keepRunning = false);
        }
    }

    private final class HandlerNotifier implements Runnable {

        private volatile boolean keepRunning = true;

        @Override
        public void run() {
            while (isActiveNotifier()) {

                //get index of handler to trigger 
                final int handlerIndex = workerPosition.getAndUpdate((i) -> {
                    //next position or back to 0 if overflow - this is not 
                    //a perfect concurrent solution, worker still needs to 
                    //handler index out of bounds exception
                    synchronized (handlers) {
                        if (i > handlers.size()) {
                            return 0;
                        }
                        return i + 1;
                    }
                });

                final HandlerContext hc;
                synchronized (handlers) {
                    try {
                        hc = handlers.get(handlerIndex);
                    } catch (IndexOutOfBoundsException ex) {
                        //a handler was removed, just skip this iteration
                        continue;
                    }
                }

                //notify outside synchronization so we don't deadlock )
                hc.notifyNext();

                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    //let while condition check worker state
                }
            }
            LOGGER.log(Level.FINE, String.format("Shutting down async "
                    + "handler notification thread %s",
                    Thread.currentThread().getName()));
        }

        private boolean isActiveNotifier() {
            return keepRunning;
        }
    }

    private final class HandlerContext {

        private final EventReader reader;
        private final EventHandler handler;
        private volatile boolean valid = true;

        private HandlerContext(EventReader reader, EventHandler handler) {
            this.reader = reader;
            this.handler = handler;
        }

        EventReader getReader() {
            return reader;
        }

        EventHandler getHandler() {
            return handler;
        }

        /**
         * Synchronously notifies the EventHandler of the provided event.
         * <p>
         * This method is intentionally synchronized so that only one worker can
         * execute this operation at a time.
         *
         * @param msg event to notify the handler about
         */
        public synchronized void notify(AppendedEventMessage msg) {
            if (!valid) {
                //handler context has been invalidated
                return;
            }

            if (msg == null) {
                return;
            }
            
            try {
                handler.handle(msg);
            } catch (StreamReadException ex) {
                //problems reading from stream
                LOGGER.log(Level.WARNING, String.format("Unable to read "
                        + "from stream '%s-%s'", reader.getChannelName(),
                        reader.getStreamName()), ex);
            } catch (Throwable ex) {
                //problems handling event
                if (msg == null) {
                    LOGGER.log(Level.SEVERE, "Unexpected null event in "
                            + "handler.");
                }
                @SuppressWarnings("null")
                final String eventType = msg.getEventType();
                final String eventSequence = msg.getSequence();

                EventHandlerAction action = handler.onFailure(msg, ex);
                switch (action) {
                    case CONTINUE:
                        LOGGER.log(Level.FINEST, () -> String.format(
                                "Handler '%s' threw exception while attempting "
                                + "to handle event '%s:%s', skipping "
                                + "event.",
                                handler.getClass().getName(),
                                eventType, eventSequence));
                        break;
                    case RETRY:
                        LOGGER.log(Level.FINER, () -> String.format(
                                "Handler '%s' threw exception while attempting to "
                                + "handle event '%s:%s', retrying.",
                                handler.getClass().getName(),
                                eventType, eventSequence));
                        notify(msg);
                        break;
                    case FAIL:
                        LOGGER.log(Level.WARNING, String.format("Handler "
                                + "'%s' handling failed.",
                                handler.getClass().getName()),
                                ex);
                        removeHandler(reader.getChannelName(),
                                reader.getStreamName(), handler);
                        break;
                    default:
                        LOGGER.log(Level.SEVERE, String.format("Unknown "
                                + "handler action '%s' found for handler "
                                + "'%s'; failing handler.",
                                action.name(), handler.getClass().getName()
                        ), ex);
                        removeHandler(reader.getChannelName(),
                                reader.getStreamName(), handler);
                }
            }
        }

        /**
         * Uses the calling thread to read from the EventReader and notify the
         * EventHandler if there is a next message.
         * <p>
         * This method is intentionally synchronized so that only one worker can
         * execute this operation at a time.
         */
        public synchronized void notifyNext() {
            if (!valid) {
                return;
            }

            try {
                notify(reader.poll().orElse(null));
            } catch (StreamReadException ex) {
                //problems reading from stream
                LOGGER.log(Level.WARNING, String.format("Unable to read "
                        + "from stream '%s-%s'", reader.getChannelName(),
                        reader.getStreamName()), ex);
            }

        }

//        @Override
//        public int compareTo(HandlerContext o) {
//            return (this.lastNotified == o.lastNotified)
//                    ? 0
//                    : (this.lastNotified > o.lastNotified)
//                            ? 1
//                            : -1;
//        }
    }
}
