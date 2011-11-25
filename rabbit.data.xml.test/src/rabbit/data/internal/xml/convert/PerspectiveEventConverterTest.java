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
package rabbit.data.internal.xml.convert;

import rabbit.data.internal.xml.convert.PerspectiveEventConverter;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;
import rabbit.data.store.model.PerspectiveEvent;

import static org.junit.Assert.assertEquals;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Interval;

/**
 * @see PerspectiveEventConverter
 */
public class PerspectiveEventConverterTest extends
    AbstractConverterTest<PerspectiveEvent, PerspectiveEventType> {

  @Override
  protected PerspectiveEventConverter createConverter() {
    return new PerspectiveEventConverter();
  }

  @Override
  public void testConvert() throws Exception {
    PerspectiveEvent event = new PerspectiveEvent(new Interval(0, 1),
        getPerspective());
    PerspectiveEventType type = converter.convert(event);
    assertEquals(event.getInterval().toDurationMillis(), type.getDuration());
    assertEquals(event.getPerspective().getId(), type.getPerspectiveId());
  }

  private IPerspectiveDescriptor getPerspective() {
    return PlatformUI.getWorkbench().getPerspectiveRegistry().getPerspectives()[0];
  }

}
