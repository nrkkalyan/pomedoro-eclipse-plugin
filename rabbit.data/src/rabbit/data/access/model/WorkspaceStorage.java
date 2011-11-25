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
package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

import org.eclipse.core.runtime.IPath;

import javax.annotation.Nullable;

/**
 * Contains information about where data is stored for a workspace.
 */
public final class WorkspaceStorage {

  private final IPath storagePath;
  private final IPath workspacePath;

  /**
   * Constructor.
   * @param storagePath The path to where the data is stored.
   * @param workspacePath The path to the workspace where the data came from, or
   *        null if unknown.
   * @throws NullPointerException If {@code storagePath} is null.
   */
  public WorkspaceStorage(IPath storagePath, @Nullable IPath workspacePath) {
    this.storagePath = checkNotNull(storagePath);
    this.workspacePath = workspacePath;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof WorkspaceStorage) {
      WorkspaceStorage ws = (WorkspaceStorage) obj;
      return Objects.equal(getStoragePath(), ws.getStoragePath())
          && Objects.equal(getWorkspacePath(), ws.getWorkspacePath());
    }
    return false;
  }

  /**
   * @return The path to where the data is stored.
   */
  public IPath getStoragePath() {
    return storagePath;
  }

  /**
   * @return The path to the workspace, may be null if unknown.
   */
  public IPath getWorkspacePath() {
    return workspacePath;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getStoragePath(), getWorkspacePath());
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .addValue(getWorkspacePath())
        .addValue(getStoragePath()).toString();
  }
}
