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

import rabbit.data.store.model.FileEvent;
import rabbit.data.store.model.TaskFileEvent;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.joda.time.Interval;
import org.junit.Test;

/**
 * @see TaskFileEvent
 */
@SuppressWarnings("restriction")
public class TaskFileEventTest extends FileEventTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_taskNull() {
    new TaskFileEvent(new Interval(0, 1), Path.fromPortableString("/p/a.txt"),
        null);
  }

  @Test
  public void testGetTask() {
    ITask task = new LocalTask("abc", "def");
    assertEquals(
        task,
        new TaskFileEvent(new Interval(0, 1), Path
            .fromPortableString("/p/a.txt"), task).getTask());
  }

  @Override
  protected final FileEvent createEvent(Interval interval, IPath filePath) {
    return createEvent(interval, filePath, new LocalTask("a", "1"));
  }

  protected TaskFileEvent createEvent(Interval interval, IPath filePath,
      ITask task) {
    return new TaskFileEvent(interval, filePath, task);
  }
}
