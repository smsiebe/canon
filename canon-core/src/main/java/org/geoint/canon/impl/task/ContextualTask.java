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
 * A task which has access to its contextual
 *
 * @author steve_siebert
 */
public interface ContextualTask {

    /**
     * Task name.
     *
     * @return the task name
     */
    String getName();

    /**
     * Start the task.
     *
     * @param context execution context
     * @throws Throwable
     */
    void start(TaskContext context) throws Throwable;

    /**
     * Stop the task.
     *
     * @throws Throwable
     */
    void stop() throws Throwable;

}
