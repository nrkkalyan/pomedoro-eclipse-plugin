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

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * Utility methods for working with viewers.
 */
public final class Viewers {

  /**
   * Creates a new, configured {@link FilteredTree}.
   * @param parent the parent composite.
   * @param filter the filter for the tree.
   * @return a {@link FilteredTree}.
   */
  public static FilteredTree newFilteredTree(
      Composite parent, PatternFilter filter) {
    GridLayoutFactory.fillDefaults().margins(0, 0).applyTo(parent);

    int style = SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL;
    @SuppressWarnings("deprecation")
    // Need this for Eclipse 3.4:
    FilteredTree tree = new FilteredTree(parent, style, filter);
    tree.setBackground(parent.getBackground());
    tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    // Make it look a bit nicer for us:
    GridLayout layout = (GridLayout) tree.getLayout();
    layout.verticalSpacing = 0;
    layout = (GridLayout) tree.getFilterControl().getParent().getLayout();
    layout.marginHeight = 5;
    layout.marginWidth = 5;

    TreeViewer viewer = tree.getViewer();
    viewer.setUseHashlookup(true);
    viewer.getTree().setHeaderVisible(true);
    expandOnDoubleClick(viewer);
    clearEmptySelection(viewer);

    return tree;
  }

  /**
   * Creates a new {@link TreeViewerColumn}.
   * @param viewer the parent viewer.
   * @param style the style of the column.
   * @param text the text of the column.
   * @param width the width of the column
   * @return a column.
   */
  public static TreeViewerColumn newTreeViewerColumn(
      TreeViewer viewer, int style, String text, int width) {
    TreeViewerColumn column = new TreeViewerColumn(viewer, style);
    column.getColumn().setResizable(true);
    column.getColumn().setMoveable(true);
    column.getColumn().setWidth(width);
    column.getColumn().setText(text);
    return column;
  }

  /**
   * Refreshes the given viewer.
   */
  public static void refresh(TreeViewer viewer) {
    viewer.getTree().setRedraw(false);
    viewer.refresh();
    viewer.getTree().setRedraw(true);
  }

  /**
   * Gets the current input of the viewer and calls
   * {@link TreeViewer#setInput(Object)} on it.
   */
  public static void resetInput(TreeViewer viewer) {
    TreePath[] paths = viewer.getExpandedTreePaths();
    viewer.getTree().setRedraw(false);
    viewer.setInput(viewer.getInput());
    viewer.setExpandedTreePaths(paths);
    viewer.getTree().setRedraw(true);
  }

  /**
   * Clears the selection when user clicks on empty area.
   */
  private static void clearEmptySelection(final TreeViewer viewer) {
    viewer.getTree().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseUp(MouseEvent e) {
        super.mouseDown(e);
        if (viewer.getTree().getItem(new Point(e.x, e.y)) == null) {
          viewer.setSelection(StructuredSelection.EMPTY);
        }
      }
    });
  }

  /**
   * Configures the given viewer to expand/collapse an tree branch when double
   * clicked on the item.
   */
  private static void expandOnDoubleClick(final TreeViewer viewer) {
    viewer.addDoubleClickListener(new IDoubleClickListener() {
      @Override
      public void doubleClick(DoubleClickEvent e) {
        if (!(e.getSelection() instanceof ITreeSelection)) {
          return;
        }

        ITreeSelection selection = (ITreeSelection) e.getSelection();
        TreePath[] paths = selection.getPaths();
        if (paths != null && paths.length > 0) {
          viewer.setExpandedState(paths[0], !viewer.getExpandedState(paths[0]));
        }
      }
    });
  }

  private Viewers() {}
}
