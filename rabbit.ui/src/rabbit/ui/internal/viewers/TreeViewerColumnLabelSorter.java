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

import com.google.common.base.Strings;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * Instances of this sorter compares elements based on their labels (ignore
 * cases).
 */
public class TreeViewerColumnLabelSorter extends TreeViewerColumnSorter {

  private final ILabelProvider labelProvider;

  /**
   * @param parent the parent viewer.
   * @param labelProvider the label provider for getting the labels of the
   *        elements.
   * @throws NullPointerException if any argument is null.
   */
  public TreeViewerColumnLabelSorter(TreeViewer parent,
      ILabelProvider labelProvider) {
    super(parent);
    this.labelProvider = checkNotNull(labelProvider);
  }

  @Override
  protected int doCompare(Viewer v, TreePath parentPath, Object e1, Object e2) {
    String s1 = Strings.nullToEmpty(labelProvider.getText(e1));
    String s2 = Strings.nullToEmpty(labelProvider.getText(e2));
    return s1.compareToIgnoreCase(s2);
  }

}
