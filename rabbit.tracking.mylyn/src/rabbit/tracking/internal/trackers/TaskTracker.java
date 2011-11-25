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

import rabbit.data.handler.DataHandler;
import rabbit.data.store.IStorer;
import rabbit.data.store.model.TaskFileEvent;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Interval;

import java.net.URI;

/**
 * Tracks task events.
 */
public class TaskTracker extends AbstractPartTracker<TaskFileEvent> {

  public TaskTracker() {
    super();
  }

  @Override
  protected IStorer<TaskFileEvent> createDataStorer() {
    return DataHandler.getStorer(TaskFileEvent.class);
  }

  @Override
  protected TaskFileEvent tryCreateEvent(long start, long end, IWorkbenchPart part) {
    ITask task = TasksUi.getTaskActivityManager().getActiveTask();
    if (task == null) {
      return null;
    }
    if (part instanceof IEditorPart) {
      IEditorInput input = ((IEditorPart) part).getEditorInput();

      /*
       * Order of this "if" statement is important.
       * 
       * The editor input is likely to implement both IFileEditorInput and 
       * IURIEditorInput. The difference between the two input is that the 
       * IFileEditorInput contains an IFile, which has a workspace path, like
       * "/project/folder/file.extension", the IUIREditorInput has no such file,
       * it only has a path to the actual file in the file system, like 
       * "C:/a.txt". We want workspace paths wherever possible.
       */
      if (input instanceof IFileEditorInput) {
        // Contains a file in the workspace
        IFile file = ((IFileEditorInput) input).getFile();
        return new TaskFileEvent(new Interval(start, end), file.getFullPath(), task);

      } else if (input instanceof IURIEditorInput) {
        // A file outside of workspace
        URI uri = ((IURIEditorInput) input).getURI();
        IPath path = new Path(uri.getPath());
        return new TaskFileEvent(new Interval(start, end), path, task);
      }
    }
    return null;
  }
}
