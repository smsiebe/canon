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
package org.geoint.canon.impl.stream;

import java.util.Collections;
import org.geoint.canon.impl.stream.EventChannels;
import org.geoint.canon.spi.stream.EventChannelProvider;
import org.geoint.canon.stream.memory.MemoryChannelProvider;
import org.geoint.canon.stream.mock.MockChannelProvider;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test that the {@link EventChannels} discovers and resolves
 * {@link EventChannelProvider} instances.
 *
 * @author steve_siebert
 */
public class ChannelsResolverTest {

    /**
     * Test EventChannels discovers both the MemoryChannelProvider and
     * MockChannelProvider providers.
     */
    @Test
    public void testProvidersDiscovered() {

        assertEquals(2, EventChannels.INSTANCE.stream()
                .filter((p)
                        -> p.provides(MemoryChannelProvider.SCHEME, Collections.EMPTY_MAP)
                        || p.provides(MockChannelProvider.SCHEME, Collections.EMPTY_MAP))
                .count());
    }
}
