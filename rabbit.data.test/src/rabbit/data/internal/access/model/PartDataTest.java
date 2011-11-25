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

import rabbit.data.access.model.IPartData;
import rabbit.data.access.model.WorkspaceStorage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

/**
 * @see PartData
 */
public class PartDataTest {
  
  private LocalDate date;
  private WorkspaceStorage workspace;
  private Duration duration;
  private String partId;
  
  @Before
  public void before() {
    date = new LocalDate().minusDays(1);
    workspace = new WorkspaceStorage(new Path(""), new Path(""));
    duration = new Duration(10);
    partId = "abc";
  }

  @Test
  public void shouldReturnNullIfKeyIsNull() {
    assertThat(
        create(date, workspace, duration, partId).get(null),
        is(nullValue()));
  }

  @Test
  public void shouldReturnTheDate() {
    assertThat(
        create(date, workspace, duration, partId).get(IPartData.DATE),
        is(date));
  }
  
  @Test
  public void shouldReturnTheDuration() {
    assertThat(
        create(date, workspace, duration, partId).get(IPartData.DURATION),
        is(duration));
  }
  
  @Test
  public void shouldReturnTheEditor() {
    IWorkbenchPartDescriptor editor = PlatformUI.getWorkbench()
        .getEditorRegistry().getDefaultEditor("a.txt");
    assertThat(
        create(date, workspace, duration, editor.getId()).getPart(), 
        is(editor));
  }
  
  @Test
  public void shouldReturnThePartId() {
    assertThat(
        create(date, workspace, duration, partId).get(IPartData.PART_ID),
        is(partId));
  }
  
  @Test
  public void shouldReturnTheView() {
    IWorkbenchPartDescriptor view = PlatformUI.getWorkbench()
        .getViewRegistry().getViews()[0];
    assertThat(
        create(date, workspace, duration, view.getId()).getPart(), 
        is(view));
  }
  
  @Test
  public void shouldReturnTheWorkspace() {
    assertThat(
        create(date, workspace, duration, partId).get(IPartData.WORKSPACE),
        is(workspace));
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADate() {
    create(null, workspace, duration, partId);
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADuration() {
    create(date, workspace, null, partId);
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAPartId() {
    create(date, workspace, duration, null);
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAWorkspace() {
    create(date, null, duration, partId);
  }
  
  /**
   * @see PartData#PartData(
   *      LocalDate, WorkspaceStorage, Duration, String)
   */
  private PartData create(
      LocalDate date, WorkspaceStorage ws, Duration dur, String partId) {
    return new PartData(date, ws, dur, partId);
  }
}
