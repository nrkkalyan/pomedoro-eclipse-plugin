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
package rabbit.data.common;

import java.util.Date;

/**
 * Represents an ID of a task.
 */
public final class TaskId {

  /*
   * Note: Mylyn re-uses local task IDs, that is, if task A has ID = 1, then the
   * user deletes A and creates task B, B's ID could be 1 as well. Therefore it
   * is necessary to compare the creation date of tasks.
   */

  private final String handleId;
  private final Date creationDate;

  /**
   * Constructs a new ID.
   * 
   * @param handleId The handle identifier of the task.
   * @param creationDate The creation date of the task.
   * @throws NullPointerException If any of the parameter is null.
   * @throws IllegalArgumentException If the handle ID is an empty string, or
   *           contains white spaces only.
   */
  public TaskId(String handleId, Date creationDate) {
    if (handleId == null || creationDate == null) {
      throw new NullPointerException();
    }
    if (handleId.trim().length() == 0) {
      throw new IllegalArgumentException();
    }
    this.handleId = handleId;
    this.creationDate = (Date) creationDate.clone();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof TaskId) {
      TaskId id = (TaskId) obj;
      return handleId.equals(id.getHandleIdentifier())
          && creationDate.equals(id.getCreationDate());
    }
    return false;
  }

  /**
   * Gets the creation date.
   * 
   * @return The creation date.
   */
  public Date getCreationDate() {
    return (Date) creationDate.clone();
  }

  /**
   * Gets the handle identifier.
   * 
   * @return The handle identifier.
   */
  public String getHandleIdentifier() {
    return handleId;
  }

  @Override
  public int hashCode() {
    return handleId.hashCode();
  }

  @Override
  public String toString() {
    return "Handle ID: " + handleId + ", creation date: "
        + creationDate.toString();
  }
}
