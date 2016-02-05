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
package org.geoint.canon.guide;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import org.geoint.canon.event.EventMessage;
import org.geoint.canon.spi.id.EventIdGenerator;
import org.geoint.canon.stream.EventChannel;
import org.geoint.guide.GUIDE;
import org.geoint.guide.InvalidGUIDEException;

/**
 * Canon random GUIDE generator for event ids.
 * <p>
 * The GUIDE id generator decides which GUIDE prefix to use based on tiered
 * degradation. If the prefix is not available at the first tier, it attempts to
 * fall back to the next tier until the prefix is found. If no prefix is found
 * the generator throws a runtime exception. The order in which the prefix is
 * determine is as follows:
 * <ol>
 * <li>Uses the value of the <i>org.geoint.canon.guide.prefix</i> header on the
 * {@link EventMessage}, if available</li>
 * <li>Uses the value of the <i>org.geoint.canon.guide.prefix</i> property on
 * the {@link EventChannel}, if available</li>
 * <li>Uses the value of the <i>org.geoint.canon.guide.prefix</i> JVM property,
 * if set</li>
 * <li>throws NoGUIDEPrefixFoundException</li>
 * </ol>
 *
 * @author steve_siebert
 */
public class GuideIdGenerator implements EventIdGenerator {

    public static final String PREFIX_NAME = "org.geoint.canon.guide.prefix";

    @Override
    public String generate(EventMessage eventContext,
            Map<String, String> channelProperties) {

        String prefix = Optional.ofNullable(
                findPrefix(eventContext.getHeaders())
                .orElseGet(() -> findPrefix(channelProperties)
                        .orElseGet(() -> findPrefix(System.getProperties())
                                .orElse(null))))
                .orElseThrow(() -> new NoGUIDEPrefixFoundException(eventContext));
        try {
            return GUIDE.randomGUIDE(Integer.valueOf(prefix)).asString();
        } catch (InvalidGUIDEException ex) {
            throw new InvalidGeneratedGUIDEException(eventContext);
        }
    }

    private Optional<String> findPrefix(Map<String, String> settings) {
        return Optional.ofNullable(validOrNull(settings.get(PREFIX_NAME)));
    }

    private Optional<String> findPrefix(Properties props) {
        return Optional.ofNullable(validOrNull(props.getProperty(PREFIX_NAME)));
    }

    private String validOrNull(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        return str;
    }
}
