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
package rabbit.data.internal.xml.store;

import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.convert.PerspectiveEventConverter;
import rabbit.data.internal.xml.merge.PerspectiveEventTypeMerger;
import rabbit.data.internal.xml.schema.events.PerspectiveEventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;
import rabbit.data.store.model.PerspectiveEvent;

import com.google.common.base.Objects;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * @see PerspectiveEventStorer
 */
public class PerspectiveEventStorerTest
    extends
    AbstractStorerTest<PerspectiveEvent, PerspectiveEventType, PerspectiveEventListType> {

  @Override
  protected PerspectiveEvent createEvent(DateTime dateTime) {
    IPerspectiveDescriptor p = PlatformUI.getWorkbench()
        .getPerspectiveRegistry().getPerspectives()[0];
    return new PerspectiveEvent(new Interval(dateTime, dateTime.plus(1)), p);
  }

  @Override
  protected PerspectiveEvent createEventDiff(DateTime dateTime) {
    IPerspectiveDescriptor p = PlatformUI.getWorkbench()
        .getPerspectiveRegistry().getPerspectives()[1];
    return new PerspectiveEvent(new Interval(dateTime, dateTime.plus(2)), p);
  }

  @Override
  protected PerspectiveEventStorer createStorer() {
    return new PerspectiveEventStorer(new PerspectiveEventConverter(),
                                      new PerspectiveEventTypeMerger(),
                                      DataStore.PERSPECTIVE_STORE);
  }

  @Override
  protected boolean equal(PerspectiveEventType t1, PerspectiveEventType t2) {
    return Objects.equal(t1.getPerspectiveId(), t2.getPerspectiveId())
        && t1.getDuration() == t2.getDuration();
  }
}
