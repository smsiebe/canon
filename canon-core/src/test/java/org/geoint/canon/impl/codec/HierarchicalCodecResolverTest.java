package org.geoint.canon.impl.codec;

import java.io.IOException;
import java.io.OutputStream;
import org.geoint.canon.codec.EventCodec;
import org.geoint.canon.codec.EventCodecException;
import org.geoint.canon.event.EventMessage;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the HierarchicalCodecResolver.
 *
 */
public class HierarchicalCodecResolverTest {

    /**
     * Test non-hierarchical codec resolver returns stored codecs and returns
     * and empty Options for unresolved types.
     */
    @Test
    public void testNonTiered() {
        HierarchicalCodecResolver codecs = new HierarchicalCodecResolver();
        codecs.add(new MockEventCodec("foo"));
        codecs.add(new MockEventCodec("bar"));

        assertTrue(codecs.findCodec("foo").isPresent());
        assertTrue(codecs.findCodec("bar").isPresent());
        assertFalse(codecs.findCodec("doesNotExist").isPresent());
    }

    /**
     * Test hierarchical codec resolver returns codecs stored at multiple tiers
     * and returns empty Options for unresolved types.
     */
    @Test
    public void testHierarchicalRetrieval() {
        final HierarchicalCodecResolver firstTier = new HierarchicalCodecResolver();
        firstTier.add(new MockEventCodec("foo"));
        final HierarchicalCodecResolver secondTier = new HierarchicalCodecResolver(firstTier);
        secondTier.add(new MockEventCodec("bar"));

        assertTrue(firstTier.findCodec("foo").isPresent());
        assertFalse(firstTier.findCodec("bar").isPresent());
        assertFalse(firstTier.findCodec("doesNotExist").isPresent());

        assertTrue(secondTier.findCodec("foo").isPresent());
        assertTrue(secondTier.findCodec("bar").isPresent());
        assertFalse(secondTier.findCodec("doesNotExist").isPresent());
    }

    /**
     * Mock codec implementation used to test HierarchicalCodecResolver.
     * <p>
     * Uses the {@link MockEventCodec#eventType} to uniquely identify the
     * resolver, used in testing the hierarchical resolution.
     */
    private class MockEventCodec implements EventCodec {

        private final String eventType;

        public MockEventCodec(String eventType) {
            this.eventType = eventType;
        }

        @Override
        public boolean isSupported(String eventType) {
            return this.eventType.equalsIgnoreCase(eventType);
        }

        @Override
        public void encode(Object event, OutputStream out)
                throws IOException, EventCodecException {
            throw new UnsupportedOperationException("Not supported in mock codec.");
        }

        @Override
        public Object decode(EventMessage e)
                throws IOException, EventCodecException {
            throw new UnsupportedOperationException("Not supported in mock codec.");
        }

    }
}
