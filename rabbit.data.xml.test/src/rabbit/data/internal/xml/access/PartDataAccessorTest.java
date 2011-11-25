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

import rabbit.data.access.model.IPartData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.access.PartDataAccessor;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.PartEventListType;
import rabbit.data.internal.xml.schema.events.PartEventType;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;

import java.util.List;

/**
 * @see PartDataAccessor
 */
public class PartDataAccessorTest extends
    AbstractAccessorTest2<IPartData, PartEventType, PartEventListType> {

  @Override
  protected void assertValues(PartEventType expected, LocalDate expectedDate,
      WorkspaceStorage expectedWs, IPartData actual) {
    assertThat(actual.get(IPartData.DATE), is(expectedDate));
    assertThat(actual.get(IPartData.WORKSPACE), is(expectedWs));
    assertThat(actual.get(IPartData.PART_ID), is(expected.getPartId()));
    assertThat(actual.get(IPartData.DURATION).getMillis(),
        is(expected.getDuration()));
  }

  @Override
  protected PartDataAccessor create() {
    return new PartDataAccessor(DataStore.PART_STORE);
  }

  @Override
  protected PartEventListType createCategory() {
    return new PartEventListType();
  }

  @Override
  protected PartEventType createElement() {
    PartEventType type = new PartEventType();
    type.setDuration(11);
    type.setPartId("am.an.id");
    return type;
  }

  @Override
  protected List<PartEventListType> getCategories(EventListType events) {
    return events.getPartEvents();
  }

  @Override
  protected List<PartEventType> getElements(PartEventListType list) {
    return list.getPartEvent();
  }

}
