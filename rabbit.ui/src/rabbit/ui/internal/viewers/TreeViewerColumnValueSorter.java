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
package rabbit.ui.internal.viewers;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.primitives.Longs;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * This sorter uses a {@link IValueProvider} to get the values of each tree path
 * and sorts the paths base on the values received.
 */
public class TreeViewerColumnValueSorter extends TreeViewerColumnSorter {

  private final IValueProvider valueProvider;

  /**
   * @param viewer the parent viewer.
   * @param valueProvider the value provider for getting values of the tree
   *        paths.
   * @throws NullPointerException if any argument is null.
   */
  public TreeViewerColumnValueSorter(TreeViewer viewer,
      IValueProvider valueProvider) {
    super(viewer);
    this.valueProvider = checkNotNull(valueProvider);
  }

  @Override
  public int doCompare(Viewer v, TreePath parentPath, Object e1, Object e2) {
    if (parentPath == null) {
      parentPath = TreePath.EMPTY;
    }
    return Longs.compare(
          valueProvider.getValue(parentPath.createChildPath(e1)),
          valueProvider.getValue(parentPath.createChildPath(e2)));
  }
}
