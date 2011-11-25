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
package rabbit.data;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.mylyn.tasks.core.ITask;
import org.junit.Test;

import java.util.Date;

public final class TasksContractTest {

  @Test
  public void getCreationDateReturnsDateCopies() throws Exception {
    final ITask task = mock(ITask.class);
    final Date date1 = TasksContract.getCreationDate(task);
    final Date date2 = TasksContract.getCreationDate(task);

    assertTrue(date1 != date2);
  }

  @Test
  public void getCreationDateShouldReturnTheEarliestDateIfTashHasNoCreationDate()
      throws Exception {

    final Date expectedDate = new Date(0);
    final ITask task = mock(ITask.class);
    given(task.getCreationDate()).willReturn(null);

    assertThat(TasksContract.getCreationDate(task), is(expectedDate));
  }

  @Test
  public void getCreationDateShouldReturnTheTaskCreationDateIfThereIsOne()
      throws Exception {

    final Date expectedDate = new Date();
    final ITask task = mock(ITask.class);
    given(task.getCreationDate()).willReturn(expectedDate);

    assertThat(TasksContract.getCreationDate(task), is(expectedDate));
  }

  @Test(expected = NullPointerException.class)
  public void getCreationDateThrowsExceptionIfTaskIsNull() throws Exception {
    TasksContract.getCreationDate(null);
  }

  @Test
  public void getEarliestDateReturnsDateCopies() throws Exception {
    final Date date1 = TasksContract.getEarliestDate();
    final Date date2 = TasksContract.getEarliestDate();
    assertTrue(date1 != date2);
  }

  @Test
  public void getEarliestDateReturnsDateWithTimeAt0() throws Exception {
    assertThat(TasksContract.getEarliestDate(), is(new Date(0)));
  }
}
