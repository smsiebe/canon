package org.geoint.canon.impl.codec;

/**
 * Mock class used to test encoding.
 * 
 * @see MockEncodableEventEncoder
 * @author steve_siebert
 */
public class MockEncodableEvent {

    private final int myInteger;

    public MockEncodableEvent(int myInteger) {
        this.myInteger = myInteger;
    }

    public int getMyInteger() {
        return myInteger;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.myInteger;
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
        final MockEncodableEvent other = (MockEncodableEvent) obj;
        if (this.myInteger != other.myInteger) {
            return false;
        }
        return true;
    }

}
