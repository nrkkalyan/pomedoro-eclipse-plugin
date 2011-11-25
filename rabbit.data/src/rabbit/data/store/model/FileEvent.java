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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.joda.time.Interval;

import java.net.URI;

/**
 * Represents a file event.
 */
public class FileEvent extends ContinuousEvent {

  private final IPath filePath;

  /**
   * Constructs a new event.
   * 
   * @param interval The interval of this event.
   * @param filePath The path of the file.
   * @throws NullPointerException If time is null or file path is null.
   * 
   * @see IResource#getFullPath()
   * @see Path#Path(String)
   * @see URI#getPath()
   */
  public FileEvent(Interval interval, IPath filePath) {
    super(interval);
    this.filePath = checkNotNull(filePath);
  }

  /**
   * Gets the file path.
   * 
   * @return The file path, never null.
   */
  public final IPath getFilePath() {
    return filePath;
  }
}
