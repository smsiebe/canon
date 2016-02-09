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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.geoint.canon.event.AppendedEventMessage;
import org.geoint.canon.stream.EventHandler;
import org.geoint.canon.stream.EventHandlerAction;
import org.geoint.canon.stream.mock.MockEventReader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author steve_siebert
 */
public class AsyncHandlerNotifierTest {

    /**
     * Tests that adding a handler works as expected.
     *
     */
    @Test
    public void testAddAndListHandler() {
        MockEventReader reader
                = new MockEventReader("notifierTest", "testAddAndListHandler");
        AsyncHandlerNotifier notifier = new AsyncHandlerNotifier();
        notifier.addHandler((e) -> {
        }, reader);
        notifier.addHandler((e) -> {
        }, reader);
        assertEquals(2, notifier.listHandlers().size());
    }

    /**
     * Tests removing a handler works as expected.
     *
     */
    @Test
    public void testAddAndRemoveHandler() {
        final String channelName = "notifierTest";
        final String streamName = "testAddAndRemoveHandler";

        MockEventReader reader = new MockEventReader(channelName, streamName);
        AsyncHandlerNotifier notifier = new AsyncHandlerNotifier();

        //add handlers
        notifier.addHandler((e) -> {
        }, reader);
        @SuppressWarnings("Convert2Lambda")
        EventHandler removableHandler = new EventHandler() {
            @Override
            public void handle(AppendedEventMessage event) throws Exception {
            }
        };
        notifier.addHandler(removableHandler, reader);

        //remove one handler
        notifier.removeHandler(channelName, streamName, removableHandler);

        assertEquals(1, notifier.listHandlers().size());
    }

    /**
     * Test that new events will be notified to handlers under normal
     * situations.
     */
    @Test
    public void testAsyncCallback() throws Exception {
        MockEventReader reader
                = new MockEventReader("notifierTest", "testAsyncCallback");
        reader.addRandomEvent();
        final AtomicInteger eventCounter = new AtomicInteger();

        AsyncHandlerNotifier notifier = null;
        try {
            notifier = new AsyncHandlerNotifier();

            notifier.addHandler((e) -> eventCounter.incrementAndGet(), reader);

        } finally {
            if (notifier != null) {
                notifier.shutdown();
            }
        }
        assertEquals(1, eventCounter.get());
    }

    /**
     * Test a handler that throws exception and instructs to skip the event.
     *
     */
    @Test
    public void testAsyncCallbackExceptionContinue() throws Exception{
        MockEventReader reader
                = new MockEventReader("notifierTest", "testAsyncCallbackExceptionContinue");
        reader.addRandomEvent("SkipEventType");
        reader.addRandomEvent();
        final AtomicInteger eventCounter = new AtomicInteger();
        final AtomicBoolean onFailureCalled = new AtomicBoolean(false);

        AsyncHandlerNotifier notifier = null;
        try {
            notifier = new AsyncHandlerNotifier();

            notifier.addHandler(new EventHandler() {
                @Override
                public void handle(AppendedEventMessage event) throws Throwable {
                    eventCounter.incrementAndGet();
                    if (event.getEventType().contentEquals("SkipEventType")) {
                        throw new RuntimeException("BLAH!");
                    }
                }

                @Override
                public EventHandlerAction onFailure(AppendedEventMessage event, Throwable ex) {
                    onFailureCalled.set(true);
                    return EventHandlerAction.CONTINUE;
                }

            }, reader);

            //allow it some time to async process
            //TODO make this more reliable
            Thread.sleep(100);
        } finally {
            if (notifier != null) {
                notifier.shutdown();
            }
        }
        assertEquals(2, eventCounter.get());
        assertTrue(onFailureCalled.get());
    }

    @Test
    public void testAsyncCallbackExceptionRetry() throws Exception{
        MockEventReader reader
                = new MockEventReader("notifierTest", "testAsyncCallbackExceptionRetry");
        reader.addRandomEvent();
        final AtomicInteger eventCounter = new AtomicInteger();

        AsyncHandlerNotifier notifier = null;
        try {
            notifier = new AsyncHandlerNotifier();

            notifier.addHandler(new EventHandler() {
                @Override
                public void handle(AppendedEventMessage event) throws Throwable {
                    if (eventCounter.incrementAndGet() < 3) {
                        throw new RuntimeException("RETRY!");
                    }
                }

                @Override
                public EventHandlerAction onFailure(AppendedEventMessage event, Throwable ex) {
                    return EventHandlerAction.RETRY;
                }

            }, reader);

            //allow it some time to async process
            //TODO make this more reliable
            Thread.sleep(100);
        } finally {
            if (notifier != null) {
                notifier.shutdown();
            }
        }
        assertEquals(3, eventCounter.get());
    }

    @Test
    public void testAsyncCallbackExceptionFail() throws Exception{
        MockEventReader reader
                = new MockEventReader("notifierTest", "testAsyncCallbackExceptionFail");
        reader.addRandomEvent();
        reader.addRandomEvent();
        reader.addRandomEvent();
        final AtomicInteger eventCounter = new AtomicInteger();

        AsyncHandlerNotifier notifier = null;
        try {
            notifier = new AsyncHandlerNotifier();

            notifier.addHandler(new EventHandler() {
                @Override
                public void handle(AppendedEventMessage event) throws Throwable {
                    eventCounter.incrementAndGet();
                    throw new RuntimeException("FAIL!");
                }

                @Override
                public EventHandlerAction onFailure(AppendedEventMessage event, Throwable ex) {
                    return EventHandlerAction.FAIL;
                }

            }, reader);

            //allow it some time to async process
            //TODO make this more reliable
            Thread.sleep(100);
        } finally {
            if (notifier != null) {
                notifier.shutdown();
            }
        }
        assertEquals(1, eventCounter.get());
    }

}
