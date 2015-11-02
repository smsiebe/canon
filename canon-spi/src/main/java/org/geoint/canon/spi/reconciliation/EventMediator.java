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
package org.geoint.canon.spi.reconciliation;

/**
 * Mediators are used by {@link EventArbitor the arbitor} to automatically
 * reconcile an {@link EventCollision}.
 * <p>
 * While the EventAbitor ultimately provides the {@link CollisionResolution},
 * mediators are used to recommend possible resolutions and may accept
 * resolutions from other mediators. Based on these recommendations, the abritor
 * will make a determination on how to reconcile the collision.
 * <p>
 * All event mediator implementations must be thread-safe. Mediator
 * implementations are recommended to avoid supporting post-construction state
 * modification.
 *
 * @author steve_siebert
 */
@FunctionalInterface
public interface EventMediator {

    /**
     * Provides a resolution recommendation for the provided collision.
     *
     * @param event collision details
     * @return recommendation
     */
    CollisionResolution mediate(EventCollision event);

    /**
     * Checked to determine if the proposed resolution is acceptable to this
     * mediator.
     * <p>
     * A mediator will only be asked about recommendations it did not create.
     * <p>
     * By default this method always returns true.
     *
     * @param collision collision
     * @param recommendation recommended resolution
     * @return true if the resolution is acceptable to this mediator, otherwise
     * false
     */
    default boolean isAcceptable(EventCollision collision,
            CollisionResolution recommendation) {
        return true;
    }
}
