package org.geoint.canon.spi.stream;

import java.util.Optional;
import org.geoint.canon.EventStream;

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
     * @param scheme stream uri scheme
     * @return true if this provider supports this scheme, otherwise false
     */
    boolean supportsScheme (String scheme);
    
    /**
     * Attempts to create an 
     * @param streamUrl
     * @return 
     */
    Optional<EventStream> findStream (String streamUrl);
    
}
