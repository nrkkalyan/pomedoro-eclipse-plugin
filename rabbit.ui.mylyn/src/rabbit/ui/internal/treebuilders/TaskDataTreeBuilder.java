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
import rabbit.data.access.model.ITaskData;
import rabbit.data.common.TaskId;
import rabbit.ui.IProvider;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.util.UnrecognizedTask;
import rabbit.ui.internal.viewers.ITreePathBuilder;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import com.google.common.base.Objects;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.joda.time.Duration;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

/**
 * A {@link TaskDataTreeBuilder} takes input as {@link ITaskDataProvider} and
 * builds tree leaves based on the order of the categories provided by the
 * {@link ICategoryProvider}, the last segment of every path will be the
 * {@link Duration} data node of each {@link ITaskData} provided by the
 * provider.
 */
public final class TaskDataTreeBuilder implements ITreePathBuilder {

  /**
   * Provides {@link ITaskData}.
   */
  public static interface ITaskDataProvider extends IProvider<ITaskData> {}

  private final ICategoryProvider provider;

  public TaskDataTreeBuilder(ICategoryProvider provider) {
    this.provider = checkNotNull(provider);
  }

  @Override
  public List<TreePath> build(Object input) {
    if (!(input instanceof ITaskDataProvider)) {
      return emptyList();
    }

    Collection<ITaskData> dataCol = ((ITaskDataProvider) input).get();
    if (dataCol == null) {
      return emptyList();
    }

    List<TreePath> result = newArrayList();
    IRepositoryModel repository = TasksUi.getRepositoryModel();

    for (ITaskData data : dataCol) {

      IFile file = data.get(ITaskData.FILE);
      List<Object> segments = newArrayList();

      for (ICategory c : provider.getSelected()) {
        if (!(c instanceof Category)) {
          continue;
        }

        switch ((Category) c) {
        case WORKSPACE:
          segments.add(data.get(ITaskData.WORKSPACE));
          break;
        case DATE:
          segments.add(data.get(ITaskData.DATE));
          break;
        case TASK:
          TaskId id = data.get(ITaskData.TASK_ID);
          ITask task = repository.getTask(id.getHandleIdentifier());
          if (task == null
              || !Objects.equal(id.getCreationDate(), TasksContract.getCreationDate(task))) {
            task = new UnrecognizedTask(id);
          }
          segments.add(task);
          break;
        case PROJECT:
          segments.add(file.getProject());
          break;
        case FOLDER:
          IContainer parent = file.getParent();
          if (!file.getProject().equals(parent)) {
            segments.add(parent);
          }
          break;
        case FILE:
          segments.add(file);
          break;
        default:
          break;
        }
      }
      segments.add(data.get(ITaskData.DURATION));
      result.add(new TreePath(segments.toArray()));
    }

    return result;
  }
}
