/*
 * Copyright 2015 Steve Siebert <steve@t-3-solutions.com>.
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
package org.geoint.canon.stream;

/**
 * Thrown if there there is a stream name collision.
 * 
 * @author Steve Siebert
 */
public class StreamAlreadyExistsException extends StreamInitializationException {

    public StreamAlreadyExistsException(String streamName) {
        super(streamName, message(streamName));
    }

    public StreamAlreadyExistsException(String streamName, String message) {
        super(streamName, message);
    }

    public StreamAlreadyExistsException(String streamName, String message, Throwable cause) {
        super(streamName, message, cause);
    }

    public StreamAlreadyExistsException(String streamName, Throwable cause) {
        super(streamName, message(streamName), cause);
    }


    private static String message(String streamName) {
        return String.format("Stream '%s' already exists.",streamName);
    }
}
