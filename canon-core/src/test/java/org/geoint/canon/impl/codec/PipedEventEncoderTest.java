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
package org.geoint.canon.impl.codec;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.geoint.canon.stream.event.StreamCreated;
import org.junit.Test;
import static org.junit.Assert.*;
        
/**
 *
 * @author steve_siebert
 */
public class PipedEventEncoderTest {

    public PipedEventEncoderTest() {
    }

    @Test
    public void testEncode() throws Exception {
        StreamCreated event = new StreamCreated("testChannel", "testStream");
        ObjectStreamEventCodec codec = new ObjectStreamEventCodec();
        
        PipedEventEncoder encoder = new PipedEventEncoder();
        
        byte[] bytes;
        try (InputStream in = encoder.encode(codec, event);
                ByteArrayOutputStream bout = new ByteArrayOutputStream();) {
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                bout.write(buffer, 0, read);
            }
            bytes = bout.toByteArray();
        }
        
        assertTrue(bytes.length > 0);
    }

}
