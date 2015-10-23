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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoint.canon.Canon;

/**
 * Manages background tasks of a Canon instance.
 *
 * @author steve_siebert
 */
public class TaskManager {

    private final Canon canon;
    private final ExecutorService executor;
    private final Collection<BackgroundTask> tasks 
            = Collections.synchronizedList(new ArrayList<>()); //used for synchronization
    private final Map<String, TaskGroup> groups = new ConcurrentHashMap<>();

    private static final Logger logger = Logger.getLogger(TaskManager.class.getName());

    public TaskManager(Canon canon) {
        this(canon, Executors.newCachedThreadPool());
    }

    public TaskManager(Canon canon, ExecutorService executor) {
        this.canon = canon;
        this.executor = executor;
    }

    /**
     * Returns all tasks registered with the task manager.
     *
     * @return unmodifiable collection of registered tasks
     */
    public Collection<BackgroundTask> getTasks() {
        return Collections.unmodifiableCollection(tasks);
    }

    /**
     * Creates a new task, registering it with this manager.
     *
     * @param taskName the task name
     * @param start executed when starting the background task
     * @param stop executed when stopping the background task
     * @return background task
     */
    public BackgroundTask register(String taskName, 
            ConsumingTask<TaskContext> start, Task stop) {

    }

    /**
     * Register a new task with the manager.
     *
     * @param task
     * @return background task
     */
    public BackgroundTask register(ContextualTask task) {

    }

    /**
     * Returns the requested task group, creating the group if it does not yet
     * exist.
     *
     * @param groupName
     * @return task group
     */
    public TaskGroup getGroup(String groupName) {
        
    }

    private static class BackgroundTaskImpl implements BackgroundTask, Runnable {

        private final TaskContext context;
        private final ContextualTask task;  //used for synchronization
        private volatile boolean running = false;
        private final Collection<Function<Throwable, Boolean>> exceptionHandlers = new ArrayList<>();
        private final Set<String> groups = new HashSet<>();

        public BackgroundTaskImpl(TaskContext context, ContextualTask task) {
            this.context = context;
            this.task = task;
        }

        @Override
        public String getName() {
            return task.getName();
        }

        /**
         * Called internal to the task manager to execute the actual task.
         *
         */
        @Override
        public void run() {
            synchronized (task) {
                if (running) {
                    return;
                }

                //start task
                running = true;
                while (running) {
                    try {
                        task.start(context);
                    } catch (Throwable ex) {
                        //report exception to handlers, shutting down the task
                        //(and related groups) if the handlers indicate the 
                        //exception is fatal
                        boolean stopTask = false;
                        logger.log(Level.FINE, ex,
                                () -> String.format("Exception thrown while "
                                        + "executing task '%s'", task.getName()));
                        for (Function<Throwable, Boolean> h : exceptionHandlers) {
                            if (h.apply(ex)) {
                                stopTask = true;
                            }
                        }
                        if (stopTask) {
                            this.stop();
                        }
                    }
                }
            }
        }

        @Override
        public TaskContext getContext() {
            return context;
        }

        @Override
        public boolean isRunning() {
            return running;
        }

        @Override
        public BackgroundTask addExceptionHandler(Function<Throwable, Boolean> handler) {
            synchronized (task) {
                exceptionHandlers.add(handler);
            }
            return this;
        }

        @Override
        public BackgroundTask addGroup(String group) {
            synchronized (task) {
                context.getTaskManager().getGroup(group).addToGroup(this);
                groups.add(group);
            }
            return this;
        }

        @Override
        public void start() {
            synchronized (task) {
                if (running) {
                    return;
                }
                logger.log(Level.FINE, () -> String.format("Starting background "
                        + "task '%s'", task.getName()));
                run();
            }
        }

        @Override
        public void stop() {
            synchronized (task) {
                if (!running) {
                    return;
                }

                logger.log(Level.FINE, "Shutting down background task '%s'",
                        task.getName());

                try {
                    this.running = false;//stop this task
                } catch (Throwable ex) {
                    exceptionHandlers.stream().forEach((h) -> h.apply(ex));
                }

                //stop all associated groups
                this.groups.stream()
                        .map((g) -> context.getTaskManager().getGroup(g))
                        .map((g) -> {
                            logger.log(Level.FINE, () -> String.format("Shutting down "
                                            + "task group '%s', caused by associated task "
                                            + "'%s' shutdown", g.getName(), task.getName()));
                            return g;
                        })
                        .forEach(TaskGroup::stop);
            }
        }

    }

    private class TaskGroupImpl implements TaskGroup {

    }
}
