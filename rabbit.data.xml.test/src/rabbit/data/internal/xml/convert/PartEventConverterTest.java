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

import rabbit.data.internal.xml.convert.PartEventConverter;
import rabbit.data.internal.xml.schema.events.PartEventType;
import rabbit.data.store.model.PartEvent;

import static org.junit.Assert.assertEquals;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Interval;

/**
 * @see PartEventConverter
 */
public class PartEventConverterTest extends
    AbstractConverterTest<PartEvent, PartEventType> {

  @Override
  protected PartEventConverter createConverter() {
    return new PartEventConverter();
  }

  @Override
  public void testConvert() throws Exception {
    PartEvent event = new PartEvent(new Interval(0, 1), getWorkbenchPart());
    PartEventType type = converter.convert(event);
    assertEquals(event.getInterval().toDurationMillis(), type.getDuration());
    assertEquals(event.getWorkbenchPart().getSite().getId(), type.getPartId());
  }

  private IWorkbenchPart getWorkbenchPart() {
    return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
        .getPartService().getActivePart();
  }

}
