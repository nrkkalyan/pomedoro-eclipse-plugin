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
package rabbit.ui.internal;

import rabbit.ui.internal.util.PageDescriptor;
import rabbit.ui.internal.viewers.PageDescriptorContentProvider;
import rabbit.ui.internal.viewers.PageDescriptorLabelProvider;

import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * A panel containing a collection of available metrics.
 */
public class MetricsPanel {

  private RabbitView view;

  /**
   * Constructor.
   * 
   * @param v The parent view.
   */
  public MetricsPanel(RabbitView v) {
    view = v;
  }

  /**
   * Creates the content.
   * 
   * @param parent The parent composite.
   */
  public void createContents(Composite parent) {
    int style = SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL;
    final TreeViewer viewer = new TreeViewer(parent, style);
    ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);
    viewer.setContentProvider(new PageDescriptorContentProvider());
    viewer.setComparator(new ViewerComparator());
    viewer.setLabelProvider(new PageDescriptorLabelProvider());
    
    viewer.getTree().addListener(SWT.MeasureItem, new Listener() {
      @Override
      public void handleEvent(Event event) {
        event.height = (event.height < 20) ? 20 : event.height;
      }
    });

    viewer.addDoubleClickListener(new IDoubleClickListener() {
      @Override
      public void doubleClick(DoubleClickEvent e) {
        IStructuredSelection select = (IStructuredSelection) e.getSelection();
        Object o = select.getFirstElement();
        if (((ITreeContentProvider) viewer.getContentProvider()).hasChildren(o)) {
          viewer.setExpandedState(o, !viewer.getExpandedState(o));
        }
      }
    });

    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = event.getSelection();
        if (selection instanceof IStructuredSelection) {
          Object element = ((IStructuredSelection) selection).getFirstElement();
          if (element != null && element instanceof PageDescriptor) {
            view.display(((PageDescriptor) element).getPage());
          }
        }
      }
    });

    viewer.setInput(RabbitUI.getDefault().loadRootPages());
    viewer.expandAll();
  }
}
