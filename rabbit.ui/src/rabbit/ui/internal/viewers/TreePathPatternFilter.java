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

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * A {@link PatternFilter} that uses {@link TreePath}s. To use this class, the
 * viewer's content provider must be an instance of
 * {@link ITreePathContentProvider}, and
 * {@link ITreePathContentProvider#getParents(Object)} must return an array that
 * contains the actual parent of a given element.
 */
public final class TreePathPatternFilter extends PatternFilter {

  private final ILabelProvider labelProvider;

  public TreePathPatternFilter(ILabelProvider labelProvider) {
    this.labelProvider = checkNotNull(labelProvider);
  }

  @Override
  protected boolean isParentMatch(Viewer viewer, Object element) {
    ITreePathContentProvider provider = (ITreePathContentProvider)
        ((ContentViewer) viewer).getContentProvider();
    TreePath[] parents = provider.getParents(element);
    if (parents == null) {
      return false;
    }

    for (TreePath parent : parents) {
      Object[] children = provider.getChildren(parent);
      for (Object child : children) {
        if (child == element) {
          TreePath currentPath = parent.createChildPath(child);
          if (isMatch(viewer, currentPath)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  protected boolean isLeafMatch(Viewer viewer, Object element) {
    String text = labelProvider.getText(element);
    if (text != null) {
      return wordMatches(text);
    }
    return false;
  }

  private boolean isMatch(Viewer viewer, TreePath path) {
    if (isLeafMatch(viewer, path.getLastSegment())) {
      return true;
    }

    ITreePathContentProvider provider = (ITreePathContentProvider)
        ((ContentViewer) viewer).getContentProvider();
    Object[] children = provider.getChildren(path);
    if (children == null) {
      return false;
    }

    for (Object child : children) {
      if (isMatch(viewer, path.createChildPath(child))) {
        return true;
      }
    }
    return false;
  }
}
