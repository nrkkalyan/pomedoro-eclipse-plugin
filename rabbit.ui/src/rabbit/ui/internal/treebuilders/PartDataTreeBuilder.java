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
import rabbit.ui.IProvider;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;
import rabbit.ui.internal.viewers.ITreePathBuilder;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewRegistry;
import org.joda.time.Duration;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

/**
 * A {@link PartDataTreeBuilder} takes input as {@link IPartDataProvider} and
 * builds tree leaves based on the order of the categories provided by the
 * {@link ICategoryProvider}, the last segment of every path will be the
 * {@link Duration} data node of each {@link IPartData} provided by the
 * provider.
 */
public class PartDataTreeBuilder implements ITreePathBuilder {

  /**
   * Provides {@link IPartData}.
   */
  public static interface IPartDataProvider extends IProvider<IPartData> {}

  private final ICategoryProvider provider;

  public PartDataTreeBuilder(ICategoryProvider provider) {
    this.provider = checkNotNull(provider);
  }

  @Override
  public List<TreePath> build(Object input) {
    if (!(input instanceof IPartDataProvider)) {
      return emptyList();
    }

    Collection<IPartData> dataCol = ((IPartDataProvider) input).get();
    if (dataCol == null) {
      return emptyList();
    }

    IWorkbench workbench = PlatformUI.getWorkbench();
    IViewRegistry viewRegistry = workbench.getViewRegistry();
    IEditorRegistry editorRegistry = workbench.getEditorRegistry();

    List<TreePath> result = newArrayList();
    for (IPartData data : dataCol) {

      List<Object> segments = newArrayList();
      for (ICategory c : provider.getSelected()) {
        if (!(c instanceof Category)) {
          continue;
        }

        switch ((Category) c) {
        case WORKSPACE:
          segments.add(data.get(IPartData.WORKSPACE));
          break;
        case DATE:
          segments.add(data.get(IPartData.DATE));
          break;
        case WORKBENCH_TOOL:
          String id = data.get(IPartData.PART_ID);
          IWorkbenchPartDescriptor part = viewRegistry.find(id);
          if (part == null) {
            part = editorRegistry.findEditor(id);
          }
          if (part == null) {
            part = new UndefinedWorkbenchPartDescriptor(id);
          }
          segments.add(part);
          break;
        default:
          break;
        }
      }
      segments.add(data.get(IPartData.DURATION));
      result.add(new TreePath(segments.toArray()));
    }

    return result;
  }

}
