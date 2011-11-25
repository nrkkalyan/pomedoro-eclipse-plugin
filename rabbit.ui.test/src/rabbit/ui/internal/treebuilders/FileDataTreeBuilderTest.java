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
package rabbit.ui.internal.treebuilders;

import rabbit.data.access.model.IFileData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.ui.IProvider;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.treebuilders.FileDataTreeBuilder.IFileDataProvider;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.viewers.ITreePathBuilder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.TreePath;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Before;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;

public final class FileDataTreeBuilderTest extends
    AbstractDataTreeBuilderTest<IFileData> {

  IWorkspaceRoot root;
  IFile fileHasParentFolder;
  LocalDate date;
  Duration duration;
  WorkspaceStorage ws;

  IFileData data;

  @Before
  public void setup() {
    root = ResourcesPlugin.getWorkspace().getRoot();
    fileHasParentFolder = root.getFile(new Path("/project/folder/file.txt"));
    duration = new Duration(100);
    date = new LocalDate().minusDays(1);
    ws = new WorkspaceStorage(new Path(".a"), new Path("/a"));

    data = mock(IFileData.class);
    given(data.get(IFileData.DATE)).willReturn(date);
    given(data.get(IFileData.DURATION)).willReturn(duration);
    given(data.get(IFileData.FILE)).willReturn(fileHasParentFolder);
    given(data.get(IFileData.WORKSPACE)).willReturn(ws);
  }

  @Override
  public void shouldCorrectlyBuildASinglePath() {
    ICategory[] categories = {
        Category.DATE,
        Category.WORKSPACE,
        Category.PROJECT,
        Category.FOLDER,
        Category.FILE};
    List<TreePath> expected = asList(newPath(
        date,
        ws,
        fileHasParentFolder.getProject(),
        fileHasParentFolder.getParent(),
        fileHasParentFolder,
        duration));

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    IFileDataProvider input = mock(IFileDataProvider.class);
    given(input.get()).willReturn(asList(data));
    List<TreePath> actual = builder.build(input);

    assertThat(toString(actual, expected), actual, equalTo(expected));
  }

  @Override
  public void shouldCorrectlyBuildMultiplePaths() {
    WorkspaceStorage ws2 = new WorkspaceStorage(new Path(".b"), null);
    LocalDate date2 = date.minusDays(2);
    Duration duration2 = duration.withMillis(10000);
    IFile fileHasNoParentFolder = root.getFile(new Path("/project/file.txt"));

    ICategory[] categories = {
        Category.PROJECT,
        Category.FOLDER,
        Category.FILE
        };
    List<TreePath> expected = asList(
        newPath(
            fileHasParentFolder.getProject(),
            fileHasParentFolder.getParent(),
            fileHasParentFolder,
            duration),
        newPath(
            fileHasNoParentFolder.getProject(),
            fileHasNoParentFolder,
            duration2));

    IFileData data2 = mock(IFileData.class);
    given(data2.get(IFileData.DATE)).willReturn(date2);
    given(data2.get(IFileData.DURATION)).willReturn(duration2);
    given(data2.get(IFileData.FILE)).willReturn(fileHasNoParentFolder);
    given(data2.get(IFileData.WORKSPACE)).willReturn(ws2);

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    IFileDataProvider input = mock(IFileDataProvider.class);
    given(input.get()).willReturn(asList(data, data2));
    List<TreePath> actual = builder.build(input);

    assertThat(actual.size(), equalTo(expected.size()));
    assertThat(toString(actual, expected),
        actual, hasItems(expected.toArray(new TreePath[0])));
  }

  @Override
  public void shouldRetainIdenticalPaths() {
    ICategory[] categories = {Category.DATE, Category.WORKSPACE};
    List<TreePath> expected = asList(
        newPath(date, ws, duration),
        newPath(date, ws, duration));

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    IFileDataProvider input = mock(IFileDataProvider.class);
    given(input.get()).willReturn(asList(data, data));
    List<TreePath> actual = builder.build(input);

    assertThat(toString(actual, expected), actual, equalTo(expected));
  }

  @Override
  protected ITreePathBuilder create(ICategoryProvider p) {
    return new FileDataTreeBuilder(p);
  }

  @Override
  protected IProvider<IFileData> input(
      final Collection<IFileData> inputData) {
    return new IFileDataProvider() {
      @Override
      public Collection<IFileData> get() {
        return inputData;
      }
    };
  }

}
