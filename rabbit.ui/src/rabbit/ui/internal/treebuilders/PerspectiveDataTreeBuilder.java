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
import rabbit.ui.IProvider;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;
import rabbit.ui.internal.viewers.ITreePathBuilder;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Duration;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

/**
 * A {@link PerspectiveDataTreeBuilder} takes input as
 * {@link IPerspectiveDataProvider} and builds tree leaves based on the order of
 * the categories provided by the {@link ICategoryProvider}, the last segment
 * of every path will be the {@link Duration} data node of each
 * {@link IPerspectiveData} provided by the provider.
 */
public class PerspectiveDataTreeBuilder implements ITreePathBuilder {

  /**
   * Provides {@link IPerspectiveData}.
   */
  public static interface IPerspectiveDataProvider
      extends IProvider<IPerspectiveData> {}

  private final ICategoryProvider provider;

  public PerspectiveDataTreeBuilder(ICategoryProvider provider) {
    this.provider = checkNotNull(provider);
  }

  @Override
  public List<TreePath> build(Object input) {
    if (!(input instanceof IPerspectiveDataProvider)) {
      return emptyList();
    }

    Collection<IPerspectiveData> dataCol =
          ((IPerspectiveDataProvider) input).get();
    if (dataCol == null) {
      return emptyList();
    }

    IPerspectiveRegistry registry =
        PlatformUI.getWorkbench().getPerspectiveRegistry();

    List<TreePath> result = newArrayList();
    for (IPerspectiveData data : dataCol) {

      List<Object> segments = newArrayList();
      for (ICategory c : provider.getSelected()) {
        if (!(c instanceof Category)) {
          continue;
        }

        switch ((Category) c) {
        case WORKSPACE:
          segments.add(data.get(IPerspectiveData.WORKSPACE));
          break;
        case DATE:
          segments.add(data.get(IPerspectiveData.DATE));
          break;
        case PERSPECTIVE:
          String id = data.get(IPerspectiveData.PERSPECTIVE_ID);
          IPerspectiveDescriptor p = registry.findPerspectiveWithId(id);
          if (p == null) {
            p = new UndefinedPerspectiveDescriptor(id);
          }
          segments.add(p);
          break;
        default:
          break;
        }
      }
      segments.add(data.get(IPerspectiveData.DURATION));
      result.add(new TreePath(segments.toArray()));
    }

    return result;
  }

}
