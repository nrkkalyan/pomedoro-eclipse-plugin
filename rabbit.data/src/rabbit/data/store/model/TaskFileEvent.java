/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package rabbit.data.store.model;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.runtime.IPath;
import org.eclipse.mylyn.tasks.core.ITask;
import org.joda.time.Interval;

/**
 * Represents a task event.
 */
public class TaskFileEvent extends FileEvent {

  private final ITask task;

  /**
   * Constructs a new event.
   * 
   * @param interval The time interval.
   * @param filePath The path of the file.
   * @param task The task that was working on.
   * @throws NullPointerException If any of the arguments are null.
   */
  public TaskFileEvent(Interval interval, IPath filePath, ITask task) {
    super(interval, filePath);
    this.task = checkNotNull(task);
  }

  /**
   * Gets the task.
   * 
   * @return The task.
   */
  public final ITask getTask() {
    return task;
  }
}
