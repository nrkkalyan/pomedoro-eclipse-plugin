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
package rabbit.ui.internal.pages;

import rabbit.ui.internal.viewers.TreeViewerColumnLabelSorter;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.joda.time.LocalDate;

/**
 * This sorter sorts elements base on their labels, except when it encounters two {@link LocalDate}
 * elements, then the two elements will be sorted by {@link LocalDate#compareTo(Object)}.
 */
public class InternalTreeViewerColumnLabelSorter extends TreeViewerColumnLabelSorter {

  public InternalTreeViewerColumnLabelSorter(TreeViewer parent, ILabelProvider labelProvider) {
    super(parent, labelProvider);
  }

  @Override
  protected int doCompare(Viewer v, TreePath parentPath, Object e1, Object e2) {
    if ((e1 instanceof LocalDate) & (e2 instanceof LocalDate)) {
      return ((LocalDate) e1).compareTo((LocalDate) e2);
    }
    return super.doCompare(v, parentPath, e1, e2);
  }
}
