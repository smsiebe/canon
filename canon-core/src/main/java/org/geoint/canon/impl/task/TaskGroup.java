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
package org.geoint.canon.impl.task;

/**
 * Manages multiple tasks to ensure they are in the same operational state.
 * <p>
 * If one task on the group is stopped, all tasks on the group is stopped. If
 * the group is in a stop state, and one task is started, all tasks are started.
 *
 * @author steve_siebert
 */
public interface TaskGroup extends BackgroundTask {

    /**
     * Task group name.
     *
     * @return name of the task group
     */
    @Override
    String getName();

    /**
     * Adds the task to this group.
     *
     * @param task
     */
    void addToGroup(BackgroundTask task);

    /**
     * Removes the task from this group.
     *
     * @param task
     */
    void removeFromGroup(BackgroundTask task);
}
