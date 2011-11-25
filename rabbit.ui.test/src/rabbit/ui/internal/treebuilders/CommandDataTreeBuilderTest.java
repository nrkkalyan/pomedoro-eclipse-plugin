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

import rabbit.data.access.model.ICommandData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.ui.IProvider;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.treebuilders.CommandDataTreeBuilder.ICommandDataProvider;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.viewers.ITreePathBuilder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.TreePath;
import org.joda.time.LocalDate;
import org.junit.Before;

import static java.util.Arrays.asList;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;

/**
 * @see CommandDataTreeBuilder
 */
public class CommandDataTreeBuilderTest
    extends AbstractDataTreeBuilderTest<ICommandData> {

  int count;
  Command command;
  LocalDate date;
  WorkspaceStorage ws;

  ICommandData data;

  @Before
  public void setup() {
    command = newCommand("hello");
    count = 19;
    date = new LocalDate().minusDays(1);
    ws = new WorkspaceStorage(new Path(".a"), new Path("/a"));

    data = mock(ICommandData.class);
    given(data.get(ICommandData.DATE)).willReturn(date);
    given(data.get(ICommandData.COUNT)).willReturn(count);
    given(data.get(ICommandData.COMMAND)).willReturn(command);
    given(data.get(ICommandData.WORKSPACE)).willReturn(ws);
  }

  @Override
  public void shouldCorrectlyBuildASinglePath() {
    ICategory[] categories = {
        Category.DATE, Category.WORKSPACE, Category.COMMAND};
    List<TreePath> expected = asList(newPath(date, ws, command, count));

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    ICommandDataProvider input = mock(ICommandDataProvider.class);
    given(input.get()).willReturn(asList(data));
    List<TreePath> actual = builder.build(input);

    assertThat(toString(actual, expected), actual, equalTo(expected));
  }

  @Override
  public void shouldCorrectlyBuildMultiplePaths() {
    WorkspaceStorage ws2 = new WorkspaceStorage(new Path(".b"), null);
    LocalDate date2 = date.minusDays(2);
    Command command2 = newCommand("abc");
    int count2 = count + 10;

    ICategory[] categories = {Category.WORKSPACE, Category.COMMAND};
    List<TreePath> expected = asList(
        newPath(ws, command, count),
        newPath(ws2, command2, count2));

    ICommandData data2 = mock(ICommandData.class);
    given(data2.get(ICommandData.DATE)).willReturn(date2);
    given(data2.get(ICommandData.COUNT)).willReturn(count2);
    given(data2.get(ICommandData.COMMAND)).willReturn(command2);
    given(data2.get(ICommandData.WORKSPACE)).willReturn(ws2);

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    ICommandDataProvider input = mock(ICommandDataProvider.class);
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
        newPath(date, ws, count),
        newPath(date, ws, count));

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    ICommandDataProvider input = mock(ICommandDataProvider.class);
    given(input.get()).willReturn(asList(data, data));
    List<TreePath> actual = builder.build(input);

    assertThat(toString(actual, expected), actual, equalTo(expected));
  }

  @Override
  protected ITreePathBuilder create(ICategoryProvider p) {
    return new CommandDataTreeBuilder(p);
  }

  @Override
  protected IProvider<ICommandData> input(
      final Collection<ICommandData> inputData) {
    return new ICommandDataProvider() {
      @Override
      public Collection<ICommandData> get() {
        return inputData;
      }
    };
  }

  private Command newCommand(String id) {
    try {
      Constructor<Command> c = Command.class
          .getDeclaredConstructor(String.class);
      c.setAccessible(true);
      return c.newInstance(id);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
