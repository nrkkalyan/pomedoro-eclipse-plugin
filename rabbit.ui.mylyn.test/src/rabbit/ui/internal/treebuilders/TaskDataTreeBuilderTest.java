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

import rabbit.data.TasksContract;
import rabbit.data.access.model.IFileData;
import rabbit.data.access.model.ITaskData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.common.TaskId;
import rabbit.ui.IProvider;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.treebuilders.FileDataTreeBuilder.IFileDataProvider;
import rabbit.ui.internal.treebuilders.TaskDataTreeBuilder.ITaskDataProvider;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.util.UnrecognizedTask;
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
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@SuppressWarnings("restriction")
public final class TaskDataTreeBuilderTest
    extends AbstractDataTreeBuilderTest<IFileData> {

  private IWorkspaceRoot root;
  private IFile fileHasParentFolder;
  private ITask validTask;
  private LocalDate date;
  private Duration duration;
  private WorkspaceStorage ws;

  private ITaskData data;

  @Before
  public void setup() {
    TaskRepository repo = TasksUi.getRepositoryManager()
        .getAllRepositories().get(0);
    validTask = TasksUi.getRepositoryModel().createTask(repo, "testing");
    validTask.setCreationDate(new Date());

    root = ResourcesPlugin.getWorkspace().getRoot();
    fileHasParentFolder = root.getFile(new Path("/project/folder/file.txt"));
    duration = new Duration(100);
    date = new LocalDate().minusDays(1);
    ws = new WorkspaceStorage(new Path(".a"), new Path("/a"));

    data = mock(ITaskData.class);
    given(data.get(ITaskData.DATE)).willReturn(date);
    given(data.get(ITaskData.DURATION)).willReturn(duration);
    given(data.get(ITaskData.FILE)).willReturn(fileHasParentFolder);
    given(data.get(ITaskData.WORKSPACE)).willReturn(ws);
    given(data.get(ITaskData.TASK_ID))
        .willReturn(
            new TaskId(validTask.getHandleIdentifier(), validTask
                .getCreationDate()));
  }

  /**
   * Issue #11 - If a task is available in Eclipse's "Task List", the "Tasks"
   * page in Rabbit should correctly display the name of the task, not
   * "Unrecognized Task...".
   */
  @Test
  public void shouldHandleTaskCreationDateWithTasksContract() throws Exception {
    // Given a task has no creation date:
    validTask.setCreationDate(null);

    /*
     * And data returns the earliest date, which means the original task has no
     * creation date:
     */
    given(data.get(ITaskData.TASK_ID)).willReturn(
        new TaskId(
                validTask.getHandleIdentifier(),
                TasksContract.getEarliestDate())); // <--

    final ICategory[] categories = {Category.TASK};
    final List<TreePath> expected = asList(newPath(validTask,
        duration)); // duration is always appended.

    final ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    final ITreePathBuilder builder = create(provider);

    final ITaskDataProvider input = mock(ITaskDataProvider.class);
    given(input.get()).willReturn(asList(data));

    // When the tree path is built:
    final List<TreePath> actual = builder.build(input);

    // Then it should contain the valid task object if it understands
    // the logic of "the earliest date is the substitution of no date":
    assertThat(actual, equalTo(expected));
  }

  @Override
  public void shouldCorrectlyBuildASinglePath() {
    ICategory[] categories = {
        Category.DATE,
        Category.WORKSPACE,
        Category.TASK,
        Category.PROJECT,
        Category.FOLDER,
        Category.FILE};
    List<TreePath> expected = asList(newPath(
        date,
        ws,
        validTask,
        fileHasParentFolder.getProject(),
        fileHasParentFolder.getParent(),
        fileHasParentFolder,
        duration));

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    ITaskDataProvider input = mock(ITaskDataProvider.class);
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
    TaskId taskIdForInvalid = new TaskId(
        validTask.getHandleIdentifier(), new Date());

    ICategory[] categories = {
        Category.PROJECT,
        Category.FILE,
        Category.TASK,
        };
    List<TreePath> expected = asList(
        newPath(
            fileHasParentFolder.getProject(),
            fileHasParentFolder,
            validTask,
            duration),
        newPath(
            fileHasNoParentFolder.getProject(),
            fileHasNoParentFolder,
            new UnrecognizedTask(taskIdForInvalid),
            duration2));

    ITaskData data2 = mock(ITaskData.class);
    given(data2.get(ITaskData.DATE)).willReturn(date2);
    given(data2.get(ITaskData.DURATION)).willReturn(duration2);
    given(data2.get(ITaskData.FILE)).willReturn(fileHasNoParentFolder);
    given(data2.get(ITaskData.WORKSPACE)).willReturn(ws2);
    given(data2.get(ITaskData.TASK_ID)).willReturn(taskIdForInvalid);

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    ITaskDataProvider input = mock(ITaskDataProvider.class);
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

    ITaskDataProvider input = mock(ITaskDataProvider.class);
    given(input.get()).willReturn(asList(data, data));
    List<TreePath> actual = builder.build(input);

    assertThat(toString(actual, expected), actual, equalTo(expected));
  }

  @Override
  protected ITreePathBuilder create(ICategoryProvider p) {
    return new TaskDataTreeBuilder(p);
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
