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
package rabbit.ui.internal.viewers;

import rabbit.data.common.TaskId;
import rabbit.ui.internal.util.UnrecognizedTask;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

import java.util.Date;

@SuppressWarnings("restriction")
public class TaskLabelProviderTest extends NullLabelProviderTest {
  
  @Test
  public void getImageShouldReturnAnImageForATask() {
    ITask task = mock(ITask.class);
    assertThat(provider.getImage(task), notNullValue());
  }
  
  @Test
  public void getTextShouldReturnTheSummaryOfATask() {
    ITask task = mock(ITask.class);
    given(task.getSummary()).willReturn("a summary");
    assertThat(provider.getText(task), equalTo(task.getSummary()));
  }

  @Test
  public void getForegroundShouldReturnDarkGrayForAnUndefinedTask() {
    TaskId id = new TaskId("id", new Date());
    ITask task = new UnrecognizedTask(id);
    assertThat(provider.getForeground(task), equalTo(PlatformUI.getWorkbench()
        .getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY)));
  }

  @Override
  protected NullLabelProvider create() {
    return new TaskLabelProvider();
  }
}
