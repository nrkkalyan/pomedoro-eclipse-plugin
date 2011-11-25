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

import rabbit.data.access.model.IJavaData;
import rabbit.data.access.model.WorkspaceStorage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

/**
 * @see JavaData
 */
public class JavaDataTest {

  private IJavaElement element;
  private LocalDate date;
  private Duration duration;
  private WorkspaceStorage workspace;

  @Before
  public void before() {
    element = mock(IJavaElement.class);
    date = new LocalDate();
    duration = new Duration(10);
    workspace = new WorkspaceStorage(new Path(""), new Path(""));
  }

  @Test
  public void shouldReturnNullIfKeyIsNull() {
    assertThat(
        create(date, workspace, duration, element).get(null),
        is(nullValue()));
  }

  @Test
  public void shouldReturnTheDate() {
    assertThat(
        create(date, workspace, duration, element).get(IJavaData.DATE),
        is(date));
  }

  @Test
  public void shouldReturnTheDuration() {
    assertThat(
        create(date, workspace, duration, element).get(IJavaData.DURATION),
        is(duration));
  }

  @Test
  public void shouldReturnTheElement() {
    assertThat(
        create(date, workspace, duration, element).get(IJavaData.JAVA_ELEMENT),
        is(element));
  }

  @Test
  public void shouldReturnTheWorkspace() {
    assertThat(
        create(date, workspace, duration, element).get(IJavaData.WORKSPACE),
        is(workspace));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADate() {
    create(null, workspace, duration, element);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADuration() {
    create(date, workspace, null, element);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAnElement() {
    create(date, workspace, duration, null);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAWorkspace() {
    create(date, null, duration, element);
  }

  /**
   * @see JavaData#JavaData(LocalDate, WorkspaceStorage, Duration, IJavaElement)
   */
  private JavaData create(LocalDate date,
                          WorkspaceStorage ws,
                          Duration duration,
                          IJavaElement e) {
    return new JavaData(date, ws, duration, e);
  }
}
