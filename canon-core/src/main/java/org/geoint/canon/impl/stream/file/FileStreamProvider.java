package org.geoint.canon.impl.stream.file;

import org.geoint.canon.spi.stream.EventStreamProvider;

/**
 * Resolves events streams on the local file system.
 * 
 * @author steve_siebert
 */
public class FileStreamProvider implements EventStreamProvider{
    
    public static final String SCHEME = "FILE";
    public static final String PROPERTY_STREAM_BASEDIR = "baseDir";
    
}
