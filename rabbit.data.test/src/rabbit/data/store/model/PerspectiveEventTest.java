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

import rabbit.data.store.model.PerspectiveEvent;

import static org.junit.Assert.assertEquals;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Interval;
import org.junit.Test;

/**
 * @see PerspectiveEvent
 */
public class PerspectiveEventTest extends ContinuousEventTest {

  IPerspectiveDescriptor pers = PlatformUI.getWorkbench()
      .getPerspectiveRegistry().getPerspectives()[1];
  private PerspectiveEvent event = createEvent(new Interval(0, 1));

  @Test(expected = NullPointerException.class)
  public void testConstructor_withPerspectiveNull() {
    new PerspectiveEvent(new Interval(0, 1), null);
  }

  @Test
  public void testGetPerspective() {
    assertEquals(pers, event.getPerspective());
  }

  @Override
  protected PerspectiveEvent createEvent(Interval interval) {
    return new PerspectiveEvent(interval, PlatformUI.getWorkbench()
        .getPerspectiveRegistry().getPerspectives()[1]);
  }
}
