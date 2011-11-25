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

import rabbit.data.store.model.PartEvent;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Interval;
import org.junit.Test;

/**
 * @see PartEvent
 */
public class PartEventTest extends ContinuousEventTest {

  private IWorkbenchWindow win = getWorkbenchWindow();
  private IWorkbenchPart part = getWorkbenchWindow().getPartService()
      .getActivePart();

  private PartEvent event = createEvent(new Interval(0, 1));

  /** Gets the currently active workbench window. */
  public IWorkbenchWindow getWorkbenchWindow() {

    final IWorkbench wb = PlatformUI.getWorkbench();
    wb.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        win = wb.getActiveWorkbenchWindow();
      }
    });
    return win;
  }

  @Test(expected = NullPointerException.class)
  public void testContructor_withPartNull() {
    new PartEvent(new Interval(0, 1), null);
  }

  @Test
  public void testGetWorkbenchPart() {
    assertSame(part, event.getWorkbenchPart());
  }

  @Test
  public void testWorkbenchEvent() {
    assertNotNull(event);
  }

  @Override
  protected PartEvent createEvent(Interval interval) {
    return new PartEvent(interval, getWorkbenchWindow().getPartService()
        .getActivePart());
  }
}
