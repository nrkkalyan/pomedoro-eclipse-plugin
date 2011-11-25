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
package rabbit.data.access.model;

import com.google.common.base.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Test;

/**
 * @see WorkspaceStorage
 */
public class WorkspaceStorageTest {

  private IPath workspacePath;
  private IPath storagePath;

  @Before
  public void before() {
    workspacePath = new Path("/a/b");
    storagePath = new Path("/d");
  }

  @Test
  public void shouldAcceptIfConstructedWithoutAWorkspacePath() {
    new WorkspaceStorage(storagePath, null);
  }

  @Test
  public void shouldBeEqualIfBothPathsAreEqual() {
    WorkspaceStorage ws1 = new WorkspaceStorage(storagePath, workspacePath);
    WorkspaceStorage ws2 = new WorkspaceStorage(storagePath, workspacePath);
    assertThat(ws1.equals(ws2), is(true));
  }

  @Test
  public void shouldBeEqualToItself() {
    WorkspaceStorage ws = new WorkspaceStorage(storagePath, workspacePath);
    assertThat(ws.equals(ws), is(true));
  }

  @Test
  public void shouldGenerateHashCodeBaseOnThePaths() {
    WorkspaceStorage ws = new WorkspaceStorage(storagePath, workspacePath);
    assertThat(ws.hashCode(), is(Objects.hashCode(storagePath, workspacePath)));
  }

  @Test
  public void shouldNotBeEqualIfStoragePathsAreNotEqual() {
    WorkspaceStorage ws1 = new WorkspaceStorage(storagePath, null);
    WorkspaceStorage ws2 = new WorkspaceStorage(storagePath.append("1"), null);
    assertThat(ws1.equals(ws2), is(false));
  }

  @Test
  public void shouldNotBeEqualIfWorkspacePathsAreNotEqual() {
    WorkspaceStorage ws1 = new WorkspaceStorage(storagePath, workspacePath);
    WorkspaceStorage ws2 = new WorkspaceStorage(storagePath, null);
    assertThat(ws1.equals(ws2), is(false));
  }

  @Test
  public void shouldReturnTheStoragePath() {
    assertThat(
        new WorkspaceStorage(storagePath, workspacePath).getStoragePath(),
        is(storagePath));
  }

  @Test
  public void shouldReturnTheWorkspacePath() {
    assertThat(
        new WorkspaceStorage(storagePath, workspacePath).getWorkspacePath(),
        is(workspacePath));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAStoragePath() {
    new WorkspaceStorage(null, workspacePath);
  }
}
