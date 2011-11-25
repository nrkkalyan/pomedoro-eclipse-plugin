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
import rabbit.data.store.model.FileEvent;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Interval;

import java.net.URI;

/**
 * Tracks time spent on files.
 */
public class FileTracker extends AbstractPartTracker<FileEvent> {

  /**
   * Constructor.
   */
  public FileTracker() {
    super();
  }

  @Override
  protected IStorer<FileEvent> createDataStorer() {
    return DataHandler.getStorer(FileEvent.class);
  }

  @Override
  protected FileEvent tryCreateEvent(long start, long end, IWorkbenchPart part) {

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
        return new FileEvent(new Interval(start, end), file.getFullPath());

      } else if (input instanceof IURIEditorInput) {
        // A file outside of workspace
        URI uri = ((IURIEditorInput) input).getURI();
        IPath path = new Path(uri.getPath());
        return new FileEvent(new Interval(start, end), path);
      }
    }
    return null;
  }
}
