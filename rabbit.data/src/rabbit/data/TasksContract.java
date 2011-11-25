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
package rabbit.data;

import org.eclipse.mylyn.tasks.core.ITask;

import java.util.Date;

/**
 * Defines methods to deal with tasks in a consistent way.
 */
public final class TasksContract {

  private static final Date DATE_ZERO = new Date(0);

  /**
   * Gets the task creation date. If the task has no creation date,
   * {@link #getEarliestDate()} is returned.
   * 
   * @param task
   *          the task to get the creation date.
   * @return the task's creation date, or {@link #getEarliestDate()} if the task
   *         has no creation date.
   * @throws NullPointerException
   *           if task is <code>null</code>.
   */
  public static Date getCreationDate(ITask task) {
    if (task == null) {
      throw new NullPointerException("Task is null");
    }

    final Date date = task.getCreationDate();
    return date != null ? date : new Date(DATE_ZERO.getTime());
  }

  /**
   * Gets the earliest date {@code new Date(0)}.
   * 
   * @return the earliest date.
   */
  public static Date getEarliestDate() {
    return new Date(DATE_ZERO.getTime());
  }

  private TasksContract() {
  }
}
