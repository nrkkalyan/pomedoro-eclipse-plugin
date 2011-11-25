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

import rabbit.data.store.model.PartEvent;
import rabbit.tracking.internal.trackers.PartTracker;
import rabbit.tracking.internal.util.WorkbenchUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.joda.time.Interval;
import org.junit.Test;

import java.util.Iterator;

/**
 * Test for {@link PartTracker}
 */
public class PartTrackerTest extends AbstractPartTrackerTest<PartEvent> {

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
      // one for the newly opened editor.
      assertEquals(3, tracker.getData().size());

      Iterator<PartEvent> it = tracker.getData().iterator();
      PartEvent event = it.next();
      while (!hasSamePart(event, editor)) {
        if (!it.hasNext()) {
          fail();
        }
        event = it.next();
      }

      long start = event.getInterval().getStartMillis();
      long end = event.getInterval().getEndMillis();
      checkTime(preStart, start, postStart, preEnd, end, postEnd);
      assertTrue(hasSamePart(event, editor));

    } finally {
      win.close();
    }
  }

  @Override
  protected PartEvent createEvent() {
    return new PartEvent(new Interval(0, 1),
        WorkbenchUtil.getActiveWindow().getPartService().getActivePart());
  }

  @Override
  protected PartTracker createTracker() {
    return new PartTracker();
  }

  @Override
  protected boolean hasSamePart(PartEvent event, IWorkbenchPart part) {
    return event.getWorkbenchPart().equals(part);
  }
}
