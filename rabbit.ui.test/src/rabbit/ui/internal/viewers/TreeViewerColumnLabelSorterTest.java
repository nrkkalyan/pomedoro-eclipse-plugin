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
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.junit.Test;

/**
 * @see TreeViewerColumnLabelSorter
 */
public final class TreeViewerColumnLabelSorterTest
    extends TreeViewerColumnSorterTest {

  /**
   * Test when a column is clicked, the items in the viewer are sorted.
   */
  @Test
  public void shouldSortItemsAccordingToTheirLabelsWhenTheColumnIsSelected() {
    Object bigger = "b";
    Object smaller = "a";

    ITreeContentProvider contentProvider = mock(ITreeContentProvider.class);
    given(contentProvider.getElements(any()))
        .willReturn(new Object[]{bigger, smaller});
    ILabelProvider labelProvider = new LabelProvider();

    TreeViewer viewer = new TreeViewer(shell);
    viewer.setContentProvider(contentProvider);
    viewer.setLabelProvider(labelProvider);

    TreeViewerColumnLabelSorter sorter = create(viewer, labelProvider);
    assertThat(sorter.getSelectedColumn(), nullValue());

    Tree tree = sorter.getViewer().getTree();
    TreeColumn column = new TreeColumn(tree, SWT.NONE);

    viewer.setInput("");

    Event event = new Event();
    event.widget = column;
    SelectionEvent selectionEvent = new SelectionEvent(event);

    sorter.widgetSelected(selectionEvent);
    assertThat(tree.getSortColumn(), is(column));
    assertThat(tree.getSortDirection(), is(SWT.UP));
    assertThat(tree.getItem(0).getData(), is(smaller));
    assertThat(tree.getItem(1).getData(), is(bigger));

    sorter.widgetSelected(selectionEvent);
    assertThat(tree.getSortColumn(), is(column));
    assertThat(tree.getSortDirection(), is(SWT.DOWN));
    assertThat(tree.getItem(0).getData(), is(bigger));
    assertThat(tree.getItem(1).getData(), is(smaller));
  }

  /**
   * Test when a column is clicked, the items in the viewer are sorted, if an
   * item's label is null, it's considered the smallest value.
   */
  @Test
  public void shouldSortEmptyLabelsAsSmallest() throws Exception {
    Object bigger = "b";
    Object smaller = "";

    ITreeContentProvider contentProvider = mock(ITreeContentProvider.class);
    given(contentProvider.getElements(any()))
        .willReturn(new Object[]{bigger, smaller});
    ILabelProvider labelProvider = new LabelProvider();

    TreeViewer viewer = new TreeViewer(shell);
    viewer.setContentProvider(contentProvider);
    viewer.setLabelProvider(labelProvider);

    TreeViewerColumnLabelSorter sorter = create(viewer, labelProvider);
    assertThat(sorter.getSelectedColumn(), nullValue());

    Tree tree = sorter.getViewer().getTree();
    TreeColumn column = new TreeColumn(tree, SWT.NONE);

    viewer.setInput("");

    Event event = new Event();
    event.widget = column;
    SelectionEvent selectionEvent = new SelectionEvent(event);

    sorter.widgetSelected(selectionEvent);
    assertThat(tree.getSortColumn(), is(column));
    assertThat(tree.getSortDirection(), is(SWT.UP));
    assertThat(tree.getItem(0).getData(), is(smaller));
    assertThat(tree.getItem(1).getData(), is(bigger));

    sorter.widgetSelected(selectionEvent);
    assertThat(tree.getSortColumn(), is(column));
    assertThat(tree.getSortDirection(), is(SWT.DOWN));
    assertThat(tree.getItem(0).getData(), is(bigger));
    assertThat(tree.getItem(1).getData(), is(smaller));
  }

  @Override
  protected TreeViewerColumnLabelSorter create(TreeViewer viewer) {
    return new TreeViewerColumnLabelSorter(viewer, mock(ILabelProvider.class));
  }

  protected TreeViewerColumnLabelSorter create(
      TreeViewer viewer, ILabelProvider provider) {
    return new TreeViewerColumnLabelSorter(viewer, provider);
  }
}
