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
package rabbit.data.internal.xml.access;

import rabbit.data.access.model.IPerspectiveData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.access.PerspectiveDataAccessor;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;

import java.util.List;

/**
 * @see PerspectiveDataAccessor
 */
public class PerspectiveDataAccessorTest
    extends
    AbstractAccessorTest2<IPerspectiveData, PerspectiveEventType, PerspectiveEventListType> {

  @Override
  protected void assertValues(PerspectiveEventType expected,
      LocalDate expectedDate, WorkspaceStorage expectedWs,
      IPerspectiveData actual) {
    assertThat(actual.get(IPerspectiveData.DATE), is(expectedDate));
    assertThat(actual.get(IPerspectiveData.WORKSPACE), is(expectedWs));
    assertThat(actual.get(IPerspectiveData.PERSPECTIVE_ID),
        is(expected.getPerspectiveId()));
    assertThat(actual.get(IPerspectiveData.DURATION).getMillis(),
        is(expected.getDuration()));
  }

  @Override
  protected PerspectiveDataAccessor create() {
    return new PerspectiveDataAccessor(DataStore.PERSPECTIVE_STORE);
  }

  @Override
  protected PerspectiveEventListType createCategory() {
    return new PerspectiveEventListType();
  }

  @Override
  protected PerspectiveEventType createElement() {
    PerspectiveEventType type = new PerspectiveEventType();
    type.setDuration(11);
    type.setPerspectiveId("abc");
    return type;
  }

  @Override
  protected List<PerspectiveEventListType> getCategories(EventListType events) {
    return events.getPerspectiveEvents();
  }

  @Override
  protected List<PerspectiveEventType> getElements(PerspectiveEventListType list) {
    return list.getPerspectiveEvent();
  }

}
