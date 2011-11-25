/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.data.internal.xml.convert;

import rabbit.data.TasksContract;
import rabbit.data.internal.xml.schema.events.TaskFileEventType;
import rabbit.data.store.model.TaskFileEvent;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.tasks.core.ITask;
import org.joda.time.Interval;
import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @see TaskFileEventConverter
 */
public class TaskFileEventConverterTest extends 
    AbstractConverterTest<TaskFileEvent, TaskFileEventType> {

  @Override
  protected TaskFileEventConverter createConverter() {
    return new TaskFileEventConverter();
  }

  @Override
  public void testConvert() throws Exception {
    final ITask task = mock(ITask.class);
    given(task.getCreationDate()).willReturn(new Date());
    final TaskFileEvent event = new TaskFileEvent(
        new Interval(0, 1), new Path("/a/b"), task);
    final TaskFileEventType type = converter.convert(event);
    
    assertEquals(
        task.getHandleIdentifier(),
        type.getTaskId().getHandleId());
    assertEquals(
        task.getCreationDate(), 
        type.getTaskId().getCreationDate().toGregorianCalendar().getTime());
    assertEquals(
        event.getInterval().toDurationMillis(), 
        type.getDuration());
    assertEquals(
        event.getFilePath().toString(),
        type.getFilePath());
  }

  /*
   * Issue #10 
   */
  @Test
  public void aTaskWithoutCreationDateShouldBeAssignedWithNonNullDateFromTasksContract() 
      throws Exception {
    
    final ITask task = mock(ITask.class);
    given(task.getCreationDate()).willReturn(null);
    
    final Date creationDate = TasksContract.getCreationDate(task);
    assertTrue(creationDate != null);
    
    final GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTimeInMillis(creationDate.getTime());
    
    final XMLGregorianCalendar expected = 
        DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

    final TaskFileEvent event = new TaskFileEvent(
        new Interval(0, 1), new Path("/a/b"), task);
    TaskFileEventType type = converter.convert(event);
    
    assertThat(type.getTaskId().getCreationDate(), is(expected));
  }
}
