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
import rabbit.ui.IProvider;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.util.LaunchName;
import rabbit.ui.internal.util.UndefinedLaunchConfigurationType;
import rabbit.ui.internal.util.UndefinedLaunchMode;
import rabbit.ui.internal.viewers.ITreePathBuilder;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.jface.viewers.TreePath;
import org.joda.time.Duration;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

/**
 * A {@link LaunchDataTreeBuilder} takes input as {@link ILaunchDataProvider}
 * and builds tree leaves based on the order of the categories provided by the
 * {@link ICategoryProvider}, the last segment of every path will be either an
 * {@link Integer} data node ({@link ILaunchData#COUNT}) or an {@link Duration}
 * data node ({@link ILaunchData#DURATION}). Each {@link ILaunchData} provided
 * by the provider will be transformed into two paths (one ends with
 * {@link Integer} and the other ends with {@link Duration}).
 */
public final class LaunchDataTreeBuilder implements ITreePathBuilder {

  private final ICategoryProvider provider;

  /**
   * Provides {@link ILaunchData}.
   */
  public static interface ILaunchDataProvider extends IProvider<ILaunchData> {}

  public LaunchDataTreeBuilder(ICategoryProvider provider) {
    this.provider = checkNotNull(provider);
  }

  @Override
  public List<TreePath> build(Object input) {
    if (!(input instanceof ILaunchDataProvider)) {
      return emptyList();
    }

    Collection<ILaunchData> dataCol = ((ILaunchDataProvider) input).get();
    if (dataCol == null) {
      return emptyList();
    }

    List<TreePath> result = newArrayList();
    ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
    for (ILaunchData data : dataCol) {

      List<Object> segments = newArrayList();
      for (ICategory category : provider.getSelected()) {
        if (!(category instanceof Category)) {
          continue;
        }

        LaunchConfigurationDescriptor d = data.get(ILaunchData.LAUNCH_CONFIG);
        switch ((Category) category) {
        case LAUNCH:
          segments.add(new LaunchName(d.getLaunchName(), d.getLaunchTypeId()));
          break;
        case LAUNCH_MODE: {
          String id = d.getLaunchModeId();
          ILaunchMode mode = manager.getLaunchMode(id);
          if (mode == null) {
            mode = new UndefinedLaunchMode(id);
          }
          segments.add(mode);
          break;
        }
        case LAUNCH_TYPE: {
          String id = d.getLaunchTypeId();
          ILaunchConfigurationType t = manager.getLaunchConfigurationType(id);
          if (t == null) {
            t = new UndefinedLaunchConfigurationType(id);
          }
          segments.add(t);
          break;
        }
        case DATE:
          segments.add(data.get(ILaunchData.DATE));
          break;
        case WORKSPACE:
          segments.add(data.get(ILaunchData.WORKSPACE));
          break;
        default:
          break;
        }
      }

      TreePath parent = new TreePath(segments.toArray());
      result.add(parent.createChildPath(data.get(ILaunchData.COUNT)));
      result.add(parent.createChildPath(data.get(ILaunchData.DURATION)));
      
      for (IFile file : data.get(ILaunchData.FILES)) {
        TreePath filePath = parent;
        
        IProject project = file.getProject();
        filePath = filePath.createChildPath(project);
        
        IContainer parentFolder = file.getParent();
        if (!project.equals(parentFolder)) {
          filePath = filePath.createChildPath(parentFolder);
        }
        filePath = filePath.createChildPath(file);
        result.add(filePath);
      }
    }
    return result;
  }
}
