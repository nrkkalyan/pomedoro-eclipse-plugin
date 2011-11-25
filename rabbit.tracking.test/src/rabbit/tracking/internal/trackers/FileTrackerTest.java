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

import rabbit.data.store.model.FileEvent;
import rabbit.tracking.internal.trackers.FileTracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.joda.time.Interval;
import org.junit.Test;

import java.util.Iterator;

/**
 * Test for {@link FileTracker}
 */
public class FileTrackerTest extends AbstractPartTrackerTest<FileEvent> {

  @Test
  public void testNewWindow() throws Exception {
    tracker.setEnabled(true);
    IWorkbenchWindow win = openNewWindow();

    try {
      long preStart = System.currentTimeMillis();
      IEditorPart editor = openNewEditor(); // Start
      long postStart = System.currentTimeMillis();

      Thread.sleep(20);

      long preEnd = System.currentTimeMillis();
      openNewEditor(); // End
      long postEnd = System.currentTimeMillis();

      // One for the original window,
      // one for the newly opened window's default active view,
      // But both are views, not editors,so they are not added,
      // one for the newly opened editor.
      assertEquals(1, tracker.getData().size());

      Iterator<FileEvent> it = tracker.getData().iterator();
      FileEvent event = it.next();
      assertTrue(hasSamePart(event, editor));

      long start = event.getInterval().getStartMillis();
      long end = event.getInterval().getEndMillis();
      checkTime(preStart, start, postStart, preEnd, end, postEnd);
      assertTrue(hasSamePart(event, editor));

    } finally {
      win.close();
    }
  }

  @Override
  protected FileEvent createEvent() {
    return new FileEvent(new Interval(0, 1),
        Path.fromPortableString("/p/f/a.txt"));
  }

  @Override
  protected FileTracker createTracker() {
    return new FileTracker();
  }

  @Override
  protected boolean hasSamePart(FileEvent event, IWorkbenchPart part) {
    if (part instanceof IEditorPart) {
      IEditorPart editor = (IEditorPart) part;
      IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
      IPath path = file.getFullPath();
      return event.getFilePath().equals(path);
    } else {
      return false;
    }
  }
}
