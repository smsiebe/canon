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

import org.geoint.canon.Canon;

/**
 * Task context.
 * 
 * @author steve_siebert
 */
public interface TaskContext {
   
    /**
     * Associated Canon instance for the task.
     * 
     * @return the canon instance related to this task
     */
    Canon getCanon();
    
    /**
     * Manager for this task,
     * 
     * @return returns the manager this task is registered with, or null if 
     * the task is not managed
     */
    TaskManager getTaskManager();
    
}
