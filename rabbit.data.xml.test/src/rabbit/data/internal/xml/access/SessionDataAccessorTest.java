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

import rabbit.data.access.model.ISessionData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.DatatypeUtil;
import rabbit.data.internal.xml.access.SessionDataAccessor;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.SessionEventListType;
import rabbit.data.internal.xml.schema.events.SessionEventType;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;

import java.util.List;

/**
 * @see SessionDataAccessor
 */
public class SessionDataAccessorTest extends
    AbstractAccessorTest2<ISessionData, SessionEventType, SessionEventListType> {

  @Override
  protected void assertValues(SessionEventType expected,
      LocalDate expectedDate, WorkspaceStorage expectedWs, ISessionData actual) {
    assertThat(actual.get(ISessionData.DATE), is(expectedDate));
    assertThat(actual.get(ISessionData.WORKSPACE), is(expectedWs));
    assertThat(actual.get(ISessionData.DURATION).getMillis(),
        is(expected.getDuration()));
  }

  @Override
  protected SessionDataAccessor create() {
    return new SessionDataAccessor(DataStore.SESSION_STORE);
  }

  @Override
  protected SessionEventListType createCategory() {
    SessionEventListType list = new SessionEventListType();
    list.setDate(DatatypeUtil.toXmlDate(new LocalDate()));
    return list;
  }

  @Override
  protected SessionEventType createElement() {
    SessionEventType type = new SessionEventType();
    type.setDuration(19834);
    return type;
  }

  @Override
  protected List<SessionEventListType> getCategories(EventListType events) {
    return events.getSessionEvents();
  }

  @Override
  protected List<SessionEventType> getElements(SessionEventListType list) {
    return list.getSessionEvent();
  }

}
