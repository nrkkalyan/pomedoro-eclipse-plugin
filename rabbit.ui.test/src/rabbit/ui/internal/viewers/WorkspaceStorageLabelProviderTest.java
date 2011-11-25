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

import rabbit.data.access.model.WorkspaceStorage;
import rabbit.ui.internal.viewers.WorkspaceStorageLabelProvider;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

/**
 * @see WorkspaceStorageLabelProvider
 */
public class WorkspaceStorageLabelProviderTest extends NullLabelProviderTest {

  @Test
  public void getForegroundShouldReturnAColorIfWorkspacePathIsNull() {
    IPath storage = Path.fromPortableString("/");
    WorkspaceStorage ws = new WorkspaceStorage(storage, null);
    assertThat(provider.getForeground(ws), notNullValue());
  }

  @Test
  public void getImageShouldReturnANonnullImage() {
    IPath workspace = new Path(System.getProperty("user.home"));
    IPath storage = Path.fromPortableString("/storage");
    WorkspaceStorage ws = new WorkspaceStorage(storage, workspace);
    assertThat(provider.getImage(ws), notNullValue());
  }

  @Test
  public void getTextShouldReturnTheUnknownStringIfThereIsNoWorkspacePath() {
    // Currently if the workspace is at: /home/user/workspace,
    // then the folder storing data for that workspace is named:
    // .home.user.workspace,
    // which is done by replacing all the separators with a dot.
    IPath storage = Path.fromPortableString("/Rabbit/.Hello.World");
    WorkspaceStorage ws = new WorkspaceStorage(storage, null);
    assertThat(provider.getText(ws), equalTo("Unknown"));
  }

  @Test
  public void getTextShouldReturnTheWorkspaceFolderNameIfThereIsOne() {
    IPath workspace = new Path(System.getProperty("user.home"));
    IPath storage = Path.fromPortableString("/storage");
    WorkspaceStorage ws = new WorkspaceStorage(storage, workspace);

    String expected = ws.getWorkspacePath().lastSegment();
    assertThat(provider.getText(ws), equalTo(expected));
  }

  @Override
  protected WorkspaceStorageLabelProvider create() {
    return new WorkspaceStorageLabelProvider();
  }
}
