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

import rabbit.data.access.model.IPerspectiveData;
import rabbit.data.access.model.WorkspaceStorage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

/**
 * @see PerspectiveData
 */
public class PerspectiveDataTest {
  
  private LocalDate date;
  private WorkspaceStorage workspace;
  private Duration duration;
  private String pId;
  
  @Before
  public void before() {
    date = new LocalDate().minusDays(1);
    workspace = new WorkspaceStorage(new Path(""), new Path(""));
    duration = new Duration(10);
    pId = "ijk";
  }

  @Test
  public void shouldReturnNullIfKeyIsNull() {
    assertThat(
        create(date, workspace, duration, pId).get(null),
        is(nullValue()));
  }

  @Test
  public void shouldReturnTheDate() {
    assertThat(
        create(date, workspace, duration, pId).get(IPerspectiveData.DATE),
        is(date));
  }
  
  @Test
  public void shouldReturnTheDuration() {
    assertThat(
        create(date, workspace, duration, pId).get(IPerspectiveData.DURATION),
        is(duration));
  }
  
  @Test
  public void shouldReturnThePerspective() {
    IPerspectiveDescriptor pp = PlatformUI.getWorkbench()
        .getPerspectiveRegistry().getPerspectives()[0];
    assertThat(
        create(date, workspace, duration, pp.getId()).getPerspective(), 
        is(pp));
  }
  
  @Test
  public void shouldReturnThePerspectiveId() {
    assertThat(
        create(date, workspace, duration, pId).get(IPerspectiveData.PERSPECTIVE_ID),
        is(pId));
  }
  
  @Test
  public void shouldReturnTheWorkspace() {
    assertThat(
        create(date, workspace, duration, pId).get(IPerspectiveData.WORKSPACE),
        is(workspace));
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADate() {
    create(null, workspace, duration, pId);
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADuration() {
    create(date, workspace, null, pId);
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAPerspectiveId() {
    create(date, workspace, duration, null);
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAWorkspace() {
    create(date, null, duration, pId);
  }
  
  /**
   * @see PerspectiveData#PerspectiveData(
   *      LocalDate, WorkspaceStorage, Duration, String)
   */
  private PerspectiveData create(
      LocalDate d, WorkspaceStorage ws, Duration dur, String perspectiveId) {
    return new PerspectiveData(d, ws, dur, perspectiveId);
  }
}
