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
package org.geoint.canon.replication;

/**
 * Event collision reconciliation options.
 *
 * @author steve_siebert
 */
public enum CollisionResolution {

    /**
     * Channels must commit only the first event, rolling back any channels
     * which have committed the second event and replacing it with the first.
     */
    FIRST_ONLY,
    /**
     * Channels must commit only the second event of the collision, rolling back
     * any channels which have committed the first event and replacing it with
     * the second.
     */
    SECOND_ONLY,
    /**
     * Channels must sequence the events in the order: first than second.
     * <p>
     * This option likely will cause some channels to rollback.
     */
    FIRST_THAN_SECOND,
    /**
     * Channels must sequence the events in the order: second than first.
     * <p>
     * This option likely will cause some channels to rollback.
     */
    SECOND_THAN_FIRST,
    /**
     * Channels should commit both events, sequence of the events does not
     * matter.
     * <p>
     * This is the "nicest" resolution, causing no rollback operations.
     */
    BOTH_NO_SEQUENCE,
    /**
     * Abstain from providing a resolution.
     */
    ABSTAIN,
    /**
     * Require manual reconciliation.
     */
    MANUAL;

}
