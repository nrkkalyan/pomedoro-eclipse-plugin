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
package rabbit.data.internal.access.model;

import rabbit.data.access.model.ITaskData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.common.TaskId;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * @see TaskData
 */
public class TaskDataTest {

  private IFile file;
  private TaskId taskId;
  private LocalDate date;
  private Duration duration;
  private WorkspaceStorage workspace;

  @Before
  public void prepare() {
    taskId = new TaskId("id", new Date());
    file = mock(IFile.class);
    date = new LocalDate();
    duration = new Duration(10);
    workspace = new WorkspaceStorage(new Path(""), new Path(""));
  }

  @Test
  public void shouldReturnNullIfKeyIsNull() {
    assertThat(
        create(date, workspace, duration, file, taskId).get(null),
        is(nullValue()));
  }

  @Test
  public void shouldReturnTheDate() {
    assertThat(
        create(date, workspace, duration, file, taskId).get(ITaskData.DATE),
        is(date));
  }

  @Test
  public void shouldReturnTheDuration() {
    assertThat(
        create(date, workspace, duration, file, taskId).get(ITaskData.DURATION),
        is(duration));
  }

  @Test
  public void shouldReturnTheFile() {
    assertThat(
        create(date, workspace, duration, file, taskId).get(ITaskData.FILE),
        is(file));
  }

  @Test
  public void shouldReturnTheWorkspace() {
    assertThat(
        create(date, workspace, duration, file, taskId).get(ITaskData.WORKSPACE),
        is(workspace));
  }
  
  @Test
  public void shouldReturnTheTaskId() {
    assertThat(
        create(date, workspace, duration, file, taskId).get(ITaskData.TASK_ID),
        is(taskId));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADate() {
    create(null, workspace, duration, file, taskId);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADuration() {
    create(date, workspace, null, file, taskId);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAFile() {
    create(date, workspace, duration, null, taskId);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAWorkspace() {
    create(date, null, duration, file, taskId);
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutATaskId() {
    create(date, workspace, duration, file, null);
  }

  /**
   * @see TaskData#TaskData(LocalDate, WorkspaceStorage, Duration, IFile, TaskId)
   */
  private TaskData create(LocalDate date, WorkspaceStorage workspace,
      Duration duration, IFile file, TaskId taskId) {
    return new TaskData(date, workspace, duration, file, taskId);
  }
}
