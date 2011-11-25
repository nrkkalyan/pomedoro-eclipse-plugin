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

import rabbit.data.access.model.IPartData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.ui.IProvider;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.treebuilders.PartDataTreeBuilder.IPartDataProvider;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;
import rabbit.ui.internal.viewers.ITreePathBuilder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;

public final class PartDataTreeBuilderTest extends
    AbstractDataTreeBuilderTest<IPartData> {

  IViewDescriptor view;
  LocalDate date;
  Duration duration;
  WorkspaceStorage ws;

  IPartData data;

  @Before
  public void setup() {
    duration = new Duration(100);
    view = PlatformUI.getWorkbench().getViewRegistry().getViews()[0];
    date = new LocalDate().minusDays(1);
    ws = new WorkspaceStorage(new Path(".a"), new Path("/a"));

    data = mock(IPartData.class);
    given(data.get(IPartData.DATE)).willReturn(date);
    given(data.get(IPartData.DURATION)).willReturn(duration);
    given(data.get(IPartData.PART_ID)).willReturn(view.getId());
    given(data.get(IPartData.WORKSPACE)).willReturn(ws);
  }

  @Override
  public void shouldCorrectlyBuildASinglePath() {
    ICategory[] categories = {
        Category.DATE, Category.WORKSPACE, Category.WORKBENCH_TOOL};
    List<TreePath> expected = asList(newPath(date, ws, view, duration));

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    IPartDataProvider input = mock(IPartDataProvider.class);
    given(input.get()).willReturn(asList(data));
    List<TreePath> actual = builder.build(input);

    assertThat(toString(actual, expected), actual, equalTo(expected));
  }

  @Test
  public void shouldBuildAPathWithUndefinedWorkbenchPartId() {
    IWorkbenchPartDescriptor undefined =
        new UndefinedWorkbenchPartDescriptor("abc123");
    ICategory[] categories = {
        Category.DATE, Category.WORKSPACE, Category.WORKBENCH_TOOL};
    List<TreePath> expected = asList(newPath(date, ws, undefined, duration));

    IPartData data = mock(IPartData.class);
    given(data.get(IPartData.DATE)).willReturn(date);
    given(data.get(IPartData.DURATION)).willReturn(duration);
    given(data.get(IPartData.PART_ID)).willReturn(undefined.getId());
    given(data.get(IPartData.WORKSPACE)).willReturn(ws);

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    IPartDataProvider input = mock(IPartDataProvider.class);
    given(input.get()).willReturn(asList(data));
    List<TreePath> actual = builder.build(input);

    assertThat(toString(actual, expected), actual, equalTo(expected));
  }

  @Override
  public void shouldCorrectlyBuildMultiplePaths() {
    WorkspaceStorage ws2 = new WorkspaceStorage(new Path(".b"), null);
    LocalDate date2 = date.minusDays(2);
    Duration duration2 = duration.withMillis(10000);
    IEditorDescriptor editor = PlatformUI.getWorkbench().getEditorRegistry()
        .getDefaultEditor("1.txt");

    ICategory[] categories = {Category.WORKSPACE, Category.WORKBENCH_TOOL};
    List<TreePath> expected = asList(
        newPath(ws, view, duration),
        newPath(ws2, editor, duration2));

    IPartData data2 = mock(IPartData.class);
    given(data2.get(IPartData.DATE)).willReturn(date2);
    given(data2.get(IPartData.DURATION)).willReturn(duration2);
    given(data2.get(IPartData.PART_ID)).willReturn(editor.getId());
    given(data2.get(IPartData.WORKSPACE)).willReturn(ws2);

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    IPartDataProvider input = mock(IPartDataProvider.class);
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

    IPartDataProvider input = mock(IPartDataProvider.class);
    given(input.get()).willReturn(asList(data, data));
    List<TreePath> actual = builder.build(input);

    assertThat(toString(actual, expected), actual, equalTo(expected));
  }

  @Override
  protected ITreePathBuilder create(ICategoryProvider p) {
    return new PartDataTreeBuilder(p);
  }

  @Override
  protected IProvider<IPartData> input(
      final Collection<IPartData> inputData) {
    return new IPartDataProvider() {
      @Override
      public Collection<IPartData> get() {
        return inputData;
      }
    };
  }

}
