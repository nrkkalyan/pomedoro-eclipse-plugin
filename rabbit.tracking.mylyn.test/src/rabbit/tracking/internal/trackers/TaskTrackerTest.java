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
package rabbit.tracking.internal.trackers;

import rabbit.data.store.model.TaskFileEvent;
import rabbit.tracking.internal.trackers.AbstractPartTrackerTest;
import rabbit.tracking.internal.trackers.TaskTracker;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Interval;
import org.junit.Before;

import java.util.Date;

/**
 * @see TaskTracker
 */
@SuppressWarnings("restriction")
public class TaskTrackerTest extends AbstractPartTrackerTest<TaskFileEvent> {

  private ITask task;

  @Before
  public void setUpActiveTask() {
    task = new LocalTask(System.currentTimeMillis() + "", "what?");
    task.setCreationDate(new Date());
    TasksUi.getTaskActivityManager().activateTask(task); 
  }

  @Override
  protected TaskFileEvent createEvent() {
    return new TaskFileEvent(new Interval(0, 1), new Path("/a/b/c"), task);
  }

  @Override
  protected TaskTracker createTracker() {
    return new TaskTracker();
  }

  @Override
  protected boolean hasSamePart(TaskFileEvent event, IWorkbenchPart part) {
    if (part instanceof IEditorPart) {
      IEditorPart editor = (IEditorPart) part;
      IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
      return event.getFilePath().equals(file.getFullPath());
    } else {
      return false;
    }
  }

}
