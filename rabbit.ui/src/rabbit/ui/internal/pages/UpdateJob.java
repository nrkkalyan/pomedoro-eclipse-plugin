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

import rabbit.data.access.IAccessor;
import rabbit.ui.Preference;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PlatformUI;
import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * An internal update job for updating a page.
 */
public abstract class UpdateJob<T> extends Job {

  private final TreeViewer viewer;
  private final Preference pref;
  private final IAccessor<T> accessor;

  /**
   * Constructs a new job.
   * 
   * @param viewer The tree viewer to be updated.
   * @param pref The preference for getting the data.
   * @param accessor The data accessor for getting the data from.
   * @throws NullPointerException If any of the arguments are null.
   */
  public UpdateJob(TreeViewer viewer, Preference pref, IAccessor<T> accessor) {
    super("Updating Rabbit View...");
    this.viewer = checkNotNull(viewer);
    this.pref = checkNotNull(pref);
    this.accessor = checkNotNull(accessor);
  }

  @Override
  protected IStatus run(IProgressMonitor monitor) {
    if (monitor.isCanceled()) {
      return Status.CANCEL_STATUS;
    } else {
      monitor.beginTask("Updating page...", 2);
    }

    LocalDate start = LocalDate.fromCalendarFields(pref.getStartDate());
    LocalDate end = LocalDate.fromCalendarFields(pref.getEndDate());
    final Collection<T> data = accessor.getData(start, end);
    monitor.worked(1);

    if (monitor.isCanceled()) {
      return Status.CANCEL_STATUS;
    }

    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        viewer.getTree().setRedraw(false);
        TreePath[] expandedPaths = viewer.getExpandedTreePaths();
        viewer.setInput(getInput(data));
        viewer.setExpandedTreePaths(expandedPaths);
        viewer.getTree().setRedraw(true);
      }
    });
    monitor.worked(1);
    monitor.done();
    return Status.OK_STATUS;
  }

  /**
   * When data is received from the data accessor, this method is called to get
   * the input object for the viewer.
   * 
   * @param data The data received from the data accessor.
   * @return The input object for the viewer.
   */
  protected abstract Object getInput(Collection<T> data);
}
