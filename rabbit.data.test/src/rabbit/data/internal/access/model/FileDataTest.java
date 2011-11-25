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

import rabbit.data.access.model.IFileData;
import rabbit.data.access.model.WorkspaceStorage;

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

/**
 * @see FileData
 */
public class FileDataTest {

  private IFile file;
  private LocalDate date;
  private Duration duration;
  private WorkspaceStorage workspace;

  @Before
  public void prepare() {
    file = mock(IFile.class);
    date = new LocalDate();
    duration = new Duration(10);
    workspace = new WorkspaceStorage(new Path(""), new Path(""));
  }

  @Test
  public void shouldReturnNullIfKeyIsNull() {
    assertThat(
        create(date, workspace, duration, file).get(null),
        is(nullValue()));
  }

  @Test
  public void shouldReturnTheDate() {
    assertThat(
        create(date, workspace, duration, file).get(IFileData.DATE),
        is(date));
  }

  @Test
  public void shouldReturnTheDuration() {
    assertThat(
        create(date, workspace, duration, file).get(IFileData.DURATION),
        is(duration));
  }

  @Test
  public void shouldReturnTheFile() {
    assertThat(
        create(date, workspace, duration, file).get(IFileData.FILE),
        is(file));
  }

  @Test
  public void shouldReturnTheWorkspace() {
    assertThat(
        create(date, workspace, duration, file).get(IFileData.WORKSPACE),
        is(workspace));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADate() {
    create(null, workspace, duration, file);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADuration() {
    create(date, workspace, null, file);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAFile() {
    create(date, workspace, duration, null);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAWorkspace() {
    create(date, null, duration, file);
  }

  /**
   * @see FileData#FileData(LocalDate, WorkspaceStorage, Duration, IFile)
   */
  private FileData create(LocalDate date,
                          WorkspaceStorage workspace,
                          Duration duration,
                          IFile file) {
    return new FileData(date, workspace, duration, file);
  }
}
