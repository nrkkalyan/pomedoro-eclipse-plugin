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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

public class ResourceLabelProviderTest extends NullLabelProviderTest {

  private final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

  @Test
  public void getForegroundShouldReturnDarkGrayForANonExistFile() {
    Color expectedColor = PlatformUI.getWorkbench().getDisplay()
        .getSystemColor(SWT.COLOR_DARK_GRAY);

    IFile file = mock(IFile.class);
    given(file.exists()).willReturn(false);

    assertThat(provider.getForeground(file), equalTo(expectedColor));
  }

  @Test
  public void getForegroundShouldReturnDarkGrayForANonExistFolder() {
    Color expectedColor = PlatformUI.getWorkbench().getDisplay()
        .getSystemColor(SWT.COLOR_DARK_GRAY);

    IFolder folder = mock(IFolder.class);
    given(folder.exists()).willReturn(false);

    assertThat(provider.getForeground(folder), equalTo(expectedColor));
  }

  @Test
  public void getForegroundShouldReturnDarkGrayForANonExistProject() {
    Color expectedColor = PlatformUI.getWorkbench().getDisplay()
        .getSystemColor(SWT.COLOR_DARK_GRAY);

    IProject project = mock(IProject.class);
    given(project.exists()).willReturn(false);

    assertThat(provider.getForeground(project), equalTo(expectedColor));
  }

  @Test
  public void getImageShouldReturnAnImageForAFile() {
    IFile file = root.getFile(new Path("/project/file"));
    assertThat(provider.getImage(file), notNullValue());
  }

  @Test
  public void getImageShouldReturnAnImageForAFolder() {
    IFolder folder = root.getFolder(new Path("/project/folder"));
    assertThat(provider.getImage(folder), notNullValue());
  }

  @Test
  public void getImageShouldReturnAnImageForAProject() {
    IProject project = root.getProject("project");
    assertThat(provider.getImage(project), notNullValue());
  }

  @Test
  public void getTextShouldReturnTheNameOfAFile() {
    String expectedName = "File1";
    IFile file = root.getFile(new Path("/project/folder/" + expectedName));
    assertThat(provider.getText(file), equalTo(expectedName));
  }

  @Test
  public void getTextShouldReturnTheNameOfAProject() {
    String expectedName = "Project1";
    IProject project = root.getProject(expectedName);
    assertThat(provider.getText(project), equalTo(expectedName));
  }

  @Test
  public void getTextShouldReturnTheRelativePathOfAFolder() {
    String expectedPath = "folderA/folderB";
    IFolder folder = root.getFolder(new Path("project/" + expectedPath));
    assertThat(provider.getText(folder), equalTo(expectedPath));
  }

  @Override
  protected NullLabelProvider create() {
    return new ResourceLabelProvider();
  }
}
