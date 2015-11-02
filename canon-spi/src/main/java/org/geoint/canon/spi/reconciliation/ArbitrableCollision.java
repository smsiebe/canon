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
 * An event collision that is pending reconciliation and may be resolved.
 *
 * @author steve_siebert
 */
public interface ArbitrableCollision {

    /**
     * Event collision details.
     *
     * @return collision details
     */
    EventCollision getCollision();

    /**
     * Current resolution.
     *
     * @return the current collision resolution, default is MANUAL
     */
    CollisionResolution getCurrentResolution();

    /**
     * Current reconciliation processing state for this collision.
     *
     * @return current reconciliation processing state
     */
    ReconciliationState getState();

    /**
     * Resolve the collision.
     *
     * @param resolution collision resolution
     */
    void resolve(CollisionResolution resolution);

}
