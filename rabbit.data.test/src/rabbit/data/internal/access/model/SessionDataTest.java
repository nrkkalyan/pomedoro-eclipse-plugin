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

import rabbit.data.access.model.ISessionData;
import rabbit.data.access.model.WorkspaceStorage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.core.runtime.Path;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Test;

/**
 * @see SessionData
 */
public class SessionDataTest {

  @Test
  public void shouldReturnTheDate() {
    LocalDate date = new LocalDate();
    WorkspaceStorage ws = new WorkspaceStorage(new Path(""), new Path(""));
    Duration duration = new Duration(10);
    assertThat(create(date, ws, duration).get(ISessionData.DATE), is(date));
  }
  
  @Test
  public void shouldReturnTheDuration() {
    LocalDate date = new LocalDate();
    WorkspaceStorage ws = new WorkspaceStorage(new Path(""), new Path(""));
    Duration dur = new Duration(10);
    assertThat(create(date, ws, dur).get(ISessionData.DURATION), is(dur));
  }

  @Test
  public void shouldReturnNullIfKeyIsNull() {
    LocalDate date = new LocalDate();
    WorkspaceStorage ws = new WorkspaceStorage(new Path(""), new Path(""));
    Duration dur = new Duration(10);
    assertThat(
        create(date, ws, dur).get(null),
        is(nullValue()));
  }

  @Test
  public void shouldReturnTheWorkspace() {
    LocalDate date = new LocalDate();
    WorkspaceStorage ws = new WorkspaceStorage(new Path(""), new Path(""));
    Duration duration = new Duration(10);
    assertThat(create(date, ws, duration).get(ISessionData.WORKSPACE), is(ws));
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADate() {
    LocalDate date = null;
    WorkspaceStorage ws = new WorkspaceStorage(new Path(""), new Path(""));
    Duration duration = new Duration(0);
    create(date, ws, duration);
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADuration() {
    LocalDate date = new LocalDate();
    WorkspaceStorage ws = new WorkspaceStorage(new Path(""), new Path(""));
    Duration duration = null;
    create(date, ws, duration);
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAWorkspace() {
    LocalDate date = new LocalDate();
    WorkspaceStorage ws = null;
    Duration duration = new Duration(0);
    create(date, ws, duration);
  }
  
  /**
   * @see SessionData#SessionData(LocalDate, WorkspaceStorage, Duration)
   */
  private SessionData create(LocalDate d, WorkspaceStorage ws, Duration dur) {
    return new SessionData(d, ws, dur);
  }
}
