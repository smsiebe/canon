/*
 * Copyright 2015 geoint.org.
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import org.geoint.canon.codec.EventCodec;

/**
 * EventCodec resolver that supports hierarchical tiered codec resolution.
 *
 * @author steve_siebert
 */
public class HierarchicalCodecResolver {

    private HierarchicalCodecResolver parentTier;
    private Collection<EventCodec> tierCodecs;

    public HierarchicalCodecResolver(EventCodec... codecs) {
        tierCodecs = Arrays.asList(codecs);
    }

    public Optional<EventCodec> resolve(String eventType) {
        return tierCodecs.stream()
                .filter((c) -> c.isSupported(eventType))
                .findFirst();
    }

}
