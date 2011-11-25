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

import rabbit.data.access.model.ITaskData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.DatatypeUtil;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.TaskFileEventListType;
import rabbit.data.internal.xml.schema.events.TaskFileEventType;
import rabbit.data.internal.xml.schema.events.TaskIdType;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;

import java.util.List;

/**
 * @see TaskDataAccessor
 */
public class TaskDataAccessorTest extends
    AbstractAccessorTest2<ITaskData, TaskFileEventType, TaskFileEventListType> {

  @Override
  protected void assertValues(TaskFileEventType expected,
      LocalDate expectedDate, WorkspaceStorage expectedWs, ITaskData actual) {
    assertThat(actual.get(ITaskData.DATE), is(expectedDate));
    assertThat(actual.get(ITaskData.WORKSPACE), is(expectedWs));
    assertThat(actual.get(ITaskData.FILE).getFullPath().toPortableString(),
        equalTo(expected.getFilePath()));
    assertThat(actual.get(ITaskData.TASK_ID).getHandleIdentifier(),
        equalTo(expected.getTaskId().getHandleId()));
    assertThat(
        actual.get(ITaskData.TASK_ID).getCreationDate(),
        equalTo(expected.getTaskId().getCreationDate().toGregorianCalendar().getTime()));
  }

  @Override
  protected TaskDataAccessor create() {
    return new TaskDataAccessor(DataStore.TASK_STORE);
  }

  @Override
  protected TaskFileEventListType createCategory() {
    TaskFileEventListType list = new TaskFileEventListType();
    list.setDate(DatatypeUtil.toXmlDate(new LocalDate()));
    return list;
  }

  @Override
  protected TaskFileEventType createElement() {
    TaskIdType id = new TaskIdType();
    id.setCreationDate(DatatypeUtil.toXmlDate(new LocalDate()));
    id.setHandleId("ok");

    TaskFileEventType type = new TaskFileEventType();
    type.setDuration(10);
    type.setFilePath("/project/folder/file.txt");
    type.setTaskId(id);
    return type;
  }

  @Override
  protected List<TaskFileEventListType> getCategories(EventListType events) {
    return events.getTaskFileEvents();
  }

  @Override
  protected List<TaskFileEventType> getElements(TaskFileEventListType list) {
    return list.getTaskFileEvent();
  }
}
