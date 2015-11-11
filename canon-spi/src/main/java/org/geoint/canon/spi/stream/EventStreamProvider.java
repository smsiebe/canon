package org.geoint.canon.spi.stream;

import org.geoint.canon.stream.EventStream;

/**
 * Resolves an EventStream for a specific transport type as defined by the 
 * scheme component of the stream URL.
 * 
 * @author steve_siebert
 */
public interface EventStreamProvider {
   
    /**
     * Indicates if this provider can create streams for the user-defined 
     * stream URI scheme.
     * 
     * @return URI scheme supported by this provider
     */
    String getScheme();
    
    /**
     * Returns the requested event stream.
     * 
     * @param streamUrl
     * @return 
     */
    EventStream getStream (String streamUrl);
    
}
