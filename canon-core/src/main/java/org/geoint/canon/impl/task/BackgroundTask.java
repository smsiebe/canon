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

import java.util.function.Function;

/**
 * A managed task run on a daemon thread.
 *
 * @author steve_siebert
 */
public interface BackgroundTask {

    /**
     * Task name.
     *
     * @return return the task name
     */
    String getName();

    /**
     *
     * @return true if the task is currently running, otherwise false
     */
    boolean isRunning();

    /**
     * Returns the task context, available if this task has been registered with
     * a TaskManager.
     *
     * @return context or null if the task is not registered with a TaskManager
     */
    TaskContext getContext();

    /**
     * Adds an exception handler which returns a boolean value indicating if the
     * task (or group) should shutdown or continue.
     *
     * @param handler
     * @return this task instance (fluid interface)
     */
    BackgroundTask addExceptionHandler(Function<Throwable, Boolean> handler);

    /**
     * Adds the task to the specified task manager group.
     *
     * @param group
     * @return this task instance (fluid interface)
     */
    BackgroundTask addGroup(String group);

    /**
     * Starts the task if not already started.
     * <p>
     * If this method is called on an already started task, nothing happens.
     *
     */
    void start();

    /**
     * Stops the running task.
     * <p>
     * If this method is called on an already stopped task, nothing happens.
     *
     */
    void stop();
}
