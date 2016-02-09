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
package org.geoint.canon;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.geoint.canon.event.AppendedEventMessage;
import org.geoint.canon.stream.EventReader;
import org.geoint.canon.stream.EventStream;
import org.geoint.canon.stream.event.ChannelCreated;
import org.geoint.canon.stream.event.StreamCreated;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link Canon} "gateway" interface.
 *
 * @author steve_siebert
 */
public class CanonTest {

    /**
     * Test that the initialization of a temporary Canon instance works as
     * expected.
     *
     * @throws Exception
     */
    @Test
    public void testTempInitialization() throws Exception {
        Canon canon = Canon.newTempInstance();
        EventStream adminStream = canon.getAdminStream();

        //test that the default admin channel/stream is initialized properly
        assertEquals("Default canon channel name was not used",
                Canon.DEFAULT_CANON_CHANNEL_NAME, adminStream.getChannelName());
        assertEquals("Default global canon stream name was not used",
                Canon.CANON_ADMIN_STREAM, adminStream.getName());
        assertTrue("Channel not initialized properly, channel admin "
                + "stream was not found",
                canon.getAdminChannel().findStream(Canon.CANON_ADMIN_STREAM).isPresent());

    }

    /**
     * Test that the global administrative stream receives channel and stream
     * creation events for the creation of the canon admin channel/stream.
     *
     * @throws Exception
     */
    @Test
    public void testGlobalAdminStreamInitalizationEvents() throws Exception {
        Canon canon = Canon.newTempInstance();
        try {
            EventStream globalAdminStream = canon.getAdminStream();

            EventReader events = globalAdminStream.newReader();
            //first event should be channel creation
            Optional<AppendedEventMessage> channelCreateEvent 
                    = events.poll(10, TimeUnit.SECONDS);
            if (!channelCreateEvent.isPresent()) {
                fail("Expected channel creation event not found.");
            }
            
            AppendedEventMessage channelCreated = channelCreateEvent.get();
            assertTrue(channelCreated.getEventType()
                    .contentEquals(ChannelCreated.class.getName()));

            //second event should be stream creation
            Optional<AppendedEventMessage> streamCreateEvent = events.poll();
            if (!channelCreateEvent.isPresent()) {
                fail("Expected channel creation event not found.");
            }
            AppendedEventMessage streamCreated = streamCreateEvent.get();
            assertTrue(streamCreated.getEventType()
                    .contentEquals(StreamCreated.class.getName()));
        } finally {
            canon.shutdown();
        }
    }

    /**
     * Test that the channel administration stream on the canon admin channel
     * has creational events for the same channel/stream are appened to the
     * stream.
     *
     * @throws Exception
     */
    @Test
    public void testAdminChannelAdminStreamInitializationEvents()
            throws Exception {

    }
//    @Test
//    public void testExplicityAdminChannelInitialization () {
//        fd
//    }
//    
//    @Test
//    public void testCreateChannel() {
//        fd
//    }
//    
//    @Test
//    public void testRetreiveExistingChannel(){
//        fd
//    }
//    
//    @Test
//    public void testCreateStream() {
//        fd
//    }
//    
//    @Test
//    public void testRetreiveExistingStream() {
//        fd
//    }
//    
//    @Test
//    public void testCanonInheritedCodec () {
//        fd
//    }
}
