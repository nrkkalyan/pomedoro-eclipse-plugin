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

import rabbit.data.access.model.ILaunchData;
import rabbit.data.access.model.LaunchConfigurationDescriptor;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.ui.IProvider;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.treebuilders.LaunchDataTreeBuilder.ILaunchDataProvider;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.util.LaunchName;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.jface.viewers.TreePath;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Before;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Tests for a {@link LaunchDataTreeBuilder}.
 */
public final class LaunchDataTreeBuilderTest
    extends AbstractDataTreeBuilderTest<ILaunchData> {

  IWorkspaceRoot root;
  ILaunchManager manager;
  ILaunchConfigurationType type;
  ILaunchMode mode;

  // Data fields:
  int count;
  LocalDate date;
  Duration duration;
  LaunchName launch;
  LaunchConfigurationDescriptor configDes;
  WorkspaceStorage ws;
  IFile fileHasParent; // File with parent folder
  IFile fileHasNoParent; // File with no parent folder

  // Data mock configured to return the above data:
  ILaunchData dataNode;

  @Before
  public void setup() {
    root = ResourcesPlugin.getWorkspace().getRoot();
    manager = DebugPlugin.getDefault().getLaunchManager();
    type = manager.getLaunchConfigurationTypes()[0];
    mode = manager.getLaunchModes()[0];

    count = 10;
    date = new LocalDate().minusDays(1);
    duration = new Duration(101);
    launch = new LaunchName("hello", type.getIdentifier());
    ws = new WorkspaceStorage(new Path("/a"), new Path(".a"));
    fileHasParent = root.getFile(new Path("/project/folder/folder1/file.txt"));
    fileHasNoParent = root.getFile(new Path("/project/file.txt"));
    configDes = new LaunchConfigurationDescriptor(
        launch.getLaunchName(), mode.getIdentifier(), type.getIdentifier());

    dataNode = mock(ILaunchData.class);
    given(dataNode.get(ILaunchData.COUNT)).willReturn(count);
    given(dataNode.get(ILaunchData.DATE)).willReturn(date);
    given(dataNode.get(ILaunchData.DURATION)).willReturn(duration);
    given(dataNode.get(ILaunchData.LAUNCH_CONFIG)).willReturn(configDes);
    given(dataNode.get(ILaunchData.WORKSPACE)).willReturn(ws);
    given(dataNode.get(ILaunchData.FILES)).willReturn(
        ImmutableSet.of(fileHasParent, fileHasNoParent));
  }

  @Override
  public void shouldCorrectlyBuildASinglePath() {
    ICategory[] categories = {
        Category.WORKSPACE,
        Category.DATE,
        Category.LAUNCH_TYPE,
        Category.LAUNCH_MODE,
        Category.LAUNCH};
    // Expected paths following the order of the categories:
    TreePath base = newPath(ws, date, type, mode, launch);
    TreePath expected1 = base.createChildPath(count);
    TreePath expected2 = base.createChildPath(duration);
    TreePath expected3 = base
        .createChildPath(fileHasParent.getProject())
        .createChildPath(fileHasParent.getParent())
        .createChildPath(fileHasParent);
    TreePath expected4 = base
        .createChildPath(fileHasNoParent.getProject())
        .createChildPath(fileHasNoParent);

    // Mocks:
    ILaunchDataProvider input = mock(ILaunchDataProvider.class);
    given(input.get()).willReturn(Arrays.asList(dataNode));
    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    LaunchDataTreeBuilder builder = create(provider);

    // Test:
    Collection<TreePath> actual = builder.build(input);
    assertThat(actual, hasItems(expected1, expected2, expected3, expected4));
    assertThat(actual.size(), is(4));
  }

  @Override
  public void shouldCorrectlyBuildMultiplePaths() {
    ICategory[] categories = {
        Category.WORKSPACE,
        Category.DATE,
        Category.LAUNCH_TYPE,
        Category.LAUNCH_MODE,
        Category.LAUNCH};
    // Expected paths following the order of the categories:
    TreePath base = newPath(ws, date, type, mode, launch);
    TreePath p1 = base.createChildPath(count);
    TreePath p2 = base.createChildPath(duration);
    TreePath p3 = base
        .createChildPath(fileHasParent.getProject())
        .createChildPath(fileHasParent.getParent())
        .createChildPath(fileHasParent);
    TreePath p4 = base
        .createChildPath(fileHasNoParent.getProject())
        .createChildPath(fileHasNoParent);

    // A new data node with no files:
    ILaunchData dataNode2 = mock(ILaunchData.class);
    given(dataNode2.get(ILaunchData.COUNT)).willReturn(count);
    given(dataNode2.get(ILaunchData.DATE)).willReturn(date);
    given(dataNode2.get(ILaunchData.DURATION)).willReturn(duration);
    given(dataNode2.get(ILaunchData.LAUNCH_CONFIG)).willReturn(configDes);
    given(dataNode2.get(ILaunchData.WORKSPACE)).willReturn(ws);
    given(dataNode2.get(ILaunchData.FILES)).willReturn(
        Collections.<IFile> emptySet());
    // Paths for the new data node:
    TreePath p5 = base.createChildPath(count);
    TreePath p6 = base.createChildPath(duration);

    // Mocks:
    ILaunchDataProvider input = mock(ILaunchDataProvider.class);
    given(input.get()).willReturn(Arrays.asList(dataNode, dataNode2));
    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    LaunchDataTreeBuilder builder = create(provider);

    // Test:
    Collection<TreePath> actual = builder.build(input);
    assertThat(actual, hasItems(p1, p2, p3, p4, p5, p6));
    assertThat(actual.size(), is(6));
  }

  @Override
  public void shouldRetainIdenticalPaths() {
    ICategory[] categories = {
        Category.DATE,
        Category.LAUNCH_TYPE,
        Category.LAUNCH};
    // Expected paths following the order of the categories:
    TreePath base = newPath(date, type, launch);
    TreePath expected1 = base.createChildPath(count);
    TreePath expected2 = base.createChildPath(duration);
    TreePath expected3 = base
        .createChildPath(fileHasParent.getProject())
        .createChildPath(fileHasParent.getParent())
        .createChildPath(fileHasParent);
    TreePath expected4 = base
        .createChildPath(fileHasNoParent.getProject())
        .createChildPath(fileHasNoParent);

    // Mocks:
    ILaunchDataProvider input = mock(ILaunchDataProvider.class);
    // Return same data node twice:
    given(input.get()).willReturn(asList(dataNode, dataNode));

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    LaunchDataTreeBuilder builder = create(provider);

    // Test:
    Multiset<TreePath> actual = HashMultiset.create(builder.build(input));
    assertThat(actual.count(expected1), is(2));
    assertThat(actual.count(expected2), is(2));
    assertThat(actual.count(expected3), is(2));
    assertThat(actual.count(expected4), is(2));
    assertThat(actual.size(), is(8));
  }

  @Override
  protected LaunchDataTreeBuilder create(ICategoryProvider p) {
    return new LaunchDataTreeBuilder(p);
  }

  @Override
  protected IProvider<ILaunchData> input(Collection<ILaunchData> inputData) {
    IProvider<ILaunchData> p = mock(ILaunchDataProvider.class);
    given(p.get()).willReturn(inputData);
    return p;
  }

}
