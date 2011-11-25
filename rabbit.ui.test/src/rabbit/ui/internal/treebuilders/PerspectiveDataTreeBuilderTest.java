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

import rabbit.data.access.model.IPerspectiveData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.ui.IProvider;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.treebuilders.PerspectiveDataTreeBuilder.IPerspectiveDataProvider;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;
import rabbit.ui.internal.viewers.ITreePathBuilder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;

public final class PerspectiveDataTreeBuilderTest extends
    AbstractDataTreeBuilderTest<IPerspectiveData> {

  IPerspectiveDescriptor perspective;
  LocalDate date;
  Duration duration;
  WorkspaceStorage ws;

  IPerspectiveData data;

  @Before
  public void setup() {
    duration = new Duration(100);
    perspective = PlatformUI.getWorkbench().getPerspectiveRegistry()
        .getPerspectives()[0];
    date = new LocalDate().minusDays(1);
    ws = new WorkspaceStorage(new Path(".a"), new Path("/a"));

    data = mock(IPerspectiveData.class);
    given(data.get(IPerspectiveData.DATE)).willReturn(date);
    given(data.get(IPerspectiveData.DURATION)).willReturn(duration);
    given(data.get(IPerspectiveData.WORKSPACE)).willReturn(ws);
    given(data.get(IPerspectiveData.PERSPECTIVE_ID)).willReturn(
        perspective.getId());
  }

  @Test
  public void shouldBuildAPathWithUndefinedPerspectiveId() {
    IPerspectiveDescriptor undefined =
        new UndefinedPerspectiveDescriptor("abc123");
    ICategory[] categories = {
        Category.DATE, Category.WORKSPACE, Category.PERSPECTIVE};
    List<TreePath> expected = asList(newPath(date, ws, undefined, duration));

    IPerspectiveData data = mock(IPerspectiveData.class);
    given(data.get(IPerspectiveData.DATE)).willReturn(date);
    given(data.get(IPerspectiveData.DURATION)).willReturn(duration);
    given(data.get(IPerspectiveData.WORKSPACE)).willReturn(ws);
    given(data.get(IPerspectiveData.PERSPECTIVE_ID)).willReturn(
        undefined.getId());

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    IPerspectiveDataProvider input = mock(IPerspectiveDataProvider.class);
    given(input.get()).willReturn(asList(data));
    List<TreePath> actual = builder.build(input);

    assertThat(toString(actual, expected), actual, equalTo(expected));
  }

  @Override
  public void shouldCorrectlyBuildASinglePath() {
    ICategory[] categories = {
        Category.DATE, Category.WORKSPACE, Category.PERSPECTIVE};
    List<TreePath> expected = asList(newPath(date, ws, perspective, duration));

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    IPerspectiveDataProvider input = mock(IPerspectiveDataProvider.class);
    given(input.get()).willReturn(asList(data));
    List<TreePath> actual = builder.build(input);

    assertThat(toString(actual, expected), actual, equalTo(expected));
  }

  @Override
  public void shouldCorrectlyBuildMultiplePaths() {
    WorkspaceStorage ws2 = new WorkspaceStorage(new Path(".b"), null);
    LocalDate date2 = date.minusDays(2);
    Duration duration2 = duration.withMillis(10000);
    IPerspectiveDescriptor p2 = PlatformUI.getWorkbench()
        .getPerspectiveRegistry().getPerspectives()[1];

    ICategory[] categories = {Category.WORKSPACE, Category.PERSPECTIVE};
    List<TreePath> expected = asList(
        newPath(ws, perspective, duration),
        newPath(ws2, p2, duration2));

    IPerspectiveData data2 = mock(IPerspectiveData.class);
    given(data2.get(IPerspectiveData.DATE)).willReturn(date2);
    given(data2.get(IPerspectiveData.DURATION)).willReturn(duration2);
    given(data2.get(IPerspectiveData.WORKSPACE)).willReturn(ws2);
    given(data2.get(IPerspectiveData.PERSPECTIVE_ID)).willReturn(p2.getId());

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    IPerspectiveDataProvider input = mock(IPerspectiveDataProvider.class);
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

    IPerspectiveDataProvider input = mock(IPerspectiveDataProvider.class);
    given(input.get()).willReturn(asList(data, data));
    List<TreePath> actual = builder.build(input);

    assertThat(toString(actual, expected), actual, equalTo(expected));
  }

  @Override
  protected ITreePathBuilder create(ICategoryProvider p) {
    return new PerspectiveDataTreeBuilder(p);
  }

  @Override
  protected IProvider<IPerspectiveData> input(
      final Collection<IPerspectiveData> inputData) {
    return new IPerspectiveDataProvider() {
      @Override
      public Collection<IPerspectiveData> get() {
        return inputData;
      }
    };
  }

}
