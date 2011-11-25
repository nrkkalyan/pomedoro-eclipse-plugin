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
import rabbit.ui.IProvider;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.viewers.ITreePathBuilder;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.TreePath;
import org.joda.time.Duration;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

/**
 * A {@link FileDataTreeBuilder} takes input as {@link IFileDataProvider} and
 * builds tree leaves based on the order of the categories provided by the
 * {@link ICategoryProvider}, the last segment of every path will be the
 * {@link Duration} data node of each {@link IFileData} provided by the
 * provider.
 */
public final class FileDataTreeBuilder implements ITreePathBuilder {

  /**
   * Provides {@link IFileData}.
   */
  public static interface IFileDataProvider extends IProvider<IFileData> {}

  private final ICategoryProvider provider;

  public FileDataTreeBuilder(ICategoryProvider provider) {
    this.provider = checkNotNull(provider);
  }

  @Override
  public List<TreePath> build(Object input) {
    if (!(input instanceof IFileDataProvider)) {
      return emptyList();
    }

    Collection<IFileData> dataCol = ((IFileDataProvider) input).get();
    if (dataCol == null) {
      return emptyList();
    }

    List<TreePath> result = newArrayList();
    for (IFileData data : dataCol) {

      IFile file = data.get(IFileData.FILE);
      List<Object> segments = newArrayList();

      for (ICategory c : provider.getSelected()) {
        if (!(c instanceof Category)) {
          continue;
        }

        switch ((Category) c) {
        case WORKSPACE:
          segments.add(data.get(IFileData.WORKSPACE));
          break;
        case DATE:
          segments.add(data.get(IFileData.DATE));
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
      segments.add(data.get(IFileData.DURATION));
      result.add(new TreePath(segments.toArray()));
    }

    return result;
  }
}
