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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @see TreeViewerColumnSorter
 */
public class TreeViewerColumnSorterTest {

  /**
   * Shell for creating additional viewers for testing, must not reassign this
   * variable.
   */
  protected static Shell shell;

  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
  }

  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_null() throws Exception {
    create(null);
  }

  @Test
  public void getViewerShouldReturnTheSameViewer() throws Exception {
    TreeViewer viewer = new TreeViewer(shell);
    assertThat(create(viewer).getViewer(), sameInstance(viewer));
  }

  @Test
  public void getSelectedColumnShouldReturnTheRightColumn() {
    TreeViewerColumnSorter sorter = create(new TreeViewer(shell));
    assertThat(sorter.getSelectedColumn(), nullValue());

    TreeColumn column = new TreeColumn(sorter.getViewer().getTree(), SWT.NONE);
    Event event = new Event();
    event.widget = column;
    SelectionEvent selectionEvent = new SelectionEvent(event);

    sorter.widgetSelected(selectionEvent);
    assertThat(sorter.getSelectedColumn(), sameInstance(column));
  }

  /**
   * Test that when a column is clicked, the sort indicator is updated on the
   * column.
   */
  @Test
  public void shouldUpdateTheSortIndicationOnTheColumnHeader() {
    TreeViewerColumnSorter sorter = create(new TreeViewer(shell));
    assertThat(sorter.getSelectedColumn(), nullValue());

    TreeColumn column = new TreeColumn(sorter.getViewer().getTree(), SWT.NONE);
    Event event = new Event();
    event.widget = column;
    SelectionEvent selectionEvent = new SelectionEvent(event);

    sorter.widgetSelected(selectionEvent);
    Tree tree = sorter.getViewer().getTree();
    assertThat(tree.getSortColumn(), is(column));
    assertThat(tree.getSortDirection(), is(SWT.UP));

    sorter.widgetSelected(selectionEvent);
    assertThat(tree.getSortColumn(), is(column));
    assertThat(tree.getSortDirection(), is(SWT.DOWN));
  }

  /**
   * Creates a viewer sorter for testing. Subclass should create a sorter using
   * the argument directly <strong>without</strong> checking for null.
   * 
   * @param viewer The viewer.
   * @return A viewer sorter for testing.
   */
  protected TreeViewerColumnSorter create(TreeViewer viewer) {
    return new TreeViewerColumnSorter(viewer) {
      @Override
      protected int doCompare(Viewer v, TreePath path, Object e1, Object e2) {
        return 0;
      }
    };
  }
}
