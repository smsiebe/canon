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
package org.geoint.canon.stream.handler;

import org.geoint.canon.EventMessage;
import org.geoint.canon.stream.event.ChannelRolledBack;

/**
 * Simplified EventHandler concerned only about handling application defined
 * events, ignoring administrative events such as rollback events.
 *
 * @author steve_siebert
 * @param <E>
 */
@FunctionalInterface
public interface EventObserver<E extends EventMessage> extends EventHandler<E> {

    /**
     * Does nothing on rollback.
     *
     * @param event
     */
    @Override
    default void rollback(ChannelRolledBack event) {
        //do nothing
    }
}
