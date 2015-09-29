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
package org.geoint.canon.stream.reconciliation;

import java.util.Collection;
import java.util.concurrent.Future;
import java.util.function.Predicate;

/**
 * Event reconciliation interface providing automated and manual means to
 * reconcile event collisions.
 * <p>
 * The arbitor first attempts to determines a resolution to an event collision
 * through a process of mediation. If mediation fails, the arbitor may provide a
 * resolution or (more likely) may resolve that the collision must be manually
 * reconciled, and the CollisionEvent will remain on the channel until such time
 * that it is reconciled, blocking further reading of the stream.
 * <p>
 * <h1>Collision Mediation</h1>
 * TODO explain the mediation process
 * <p>
 *
 * @author steve_siebert
 */
public interface EventArbitor {

    /**
     * Add a collision mediator for the arbitor to consult for reconciliation
     * recommendations.
     * <p>
     * Collisions previously resolved to require manual reconciliation will be
     * processed through newly added mediators.
     *
     * @param mediator
     */
    void addMediator(EventMediator mediator);

    /**
     * Add a collision mediator for the arbitor to consult for reconciliation
     * recommendations.
     * <p>
     * Collisions previously resolved to require manual reconciliation will be
     * processed through newly added mediators.
     *
     * @param mediator
     * @param filter pre-filters collisions the mediator supports
     */
    void addMediator(EventMediator mediator, Predicate<EventCollision> filter);

    /**
     * Remove a mediator from the automated arbitration process.
     *
     * @param mediator
     */
    void removeMediator(EventMediator mediator);

    /**
     * Returns all mediators currently used during reconciliation.
     *
     * @return collection of mediators or an empty collection if no mediators
     * are registered. Changes to the returned collection will not change the
     * abitor state, however any potential change to mediator state will.
     */
    Collection<EventMediator> getMediators();

    /**
     * Called to arbitrate an event collision.
     * <p>
     * Calling this method will attempt to mediate the collision and will return
     * an absolute resolution which must be implemented by the channel.
     *
     * @param collision
     * @return collision resolution
     */
    Future<CollisionResolution> arbitrate(EventCollision collision);

    /**
     * Check if there are any collision pending resolution.
     *
     * @return true if there are any events pending resolution
     */
    boolean areCollisions();

    /**
     * Returns the events pending resolution.
     *
     * @return
     */
    Collection<ArbitrableCollision> getCollisions();

}
