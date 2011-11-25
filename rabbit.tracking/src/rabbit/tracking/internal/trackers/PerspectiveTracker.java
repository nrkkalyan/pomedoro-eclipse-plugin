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
package rabbit.tracking.internal.trackers;

import rabbit.data.handler.DataHandler;
import rabbit.data.store.IStorer;
import rabbit.data.store.model.PerspectiveEvent;
import rabbit.tracking.internal.IdleDetector;
import rabbit.tracking.internal.TrackingPlugin;
import rabbit.tracking.internal.util.Recorder;
import rabbit.tracking.internal.util.WorkbenchUtil;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Interval;

import java.util.Observable;
import java.util.Observer;

import javax.annotation.Nullable;

/**
 * Tracker for tracking on perspective usage.
 */
public class PerspectiveTracker extends AbstractTracker<PerspectiveEvent> {

  /**
   * A recorder for recording the time.
   */
  private final Recorder<IPerspectiveDescriptor> recorder = new Recorder<IPerspectiveDescriptor>();

  /**
   * An observer observing on the {@link #recorder} and user activeness.
   */
  private final Observer observer = new Observer() {

    @Override
    public void update(Observable o, Object arg) {
      if (!isEnabled()) {
        return;
      }

      if (o == TrackingPlugin.getDefault().getIdleDetector()) {
        if (((IdleDetector) o).isUserActive()) {
          checkStart();
        } else {
          recorder.stop();
        }

      } else if (o == recorder) {
        long start = recorder.getLastRecord().getStartTimeMillis();
        long end = recorder.getLastRecord().getEndTimeMillis();
        IPerspectiveDescriptor p = recorder.getLastRecord().getUserData();
        addData(new PerspectiveEvent(new Interval(start, end), p));
      }
    }
  };

  /**
   * A perspective listener for tracking time spent on perspectives.
   */
  private final IPerspectiveListener persplistener = new PerspectiveAdapter() {

    @Override
    public void perspectiveActivated(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
      recorder.start(perspective);
    }

    @Override
    public void perspectiveDeactivated(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
      recorder.stop();
    }
  };

  private final IWindowListener winlistener = new IWindowListener() {

    @Override
    public void windowActivated(IWorkbenchWindow win) {
      recorder.start(WorkbenchUtil.getPerspective(win));
    }

    @Override
    public void windowClosed(IWorkbenchWindow win) {
      win.removePerspectiveListener(persplistener);
      recorder.stop();
    }

    @Override
    public void windowDeactivated(IWorkbenchWindow win) {
      recorder.stop();
    }

    @Override
    public void windowOpened(IWorkbenchWindow win) {
      win.addPerspectiveListener(persplistener);
      if (WorkbenchUtil.isActiveShell(win)) {
        checkStart(win);
      }
    }
  };

  /**
   * Constructor.
   */
  public PerspectiveTracker() {
    recorder.addObserver(observer);
  }

  @Override
  protected IStorer<PerspectiveEvent> createDataStorer() {
    return DataHandler.getStorer(PerspectiveEvent.class);
  }

  @Override
  protected void doDisable() {
    recorder.stop();
    for (IWorkbenchWindow win : getWorkbenchWindows()) {
      win.removePerspectiveListener(persplistener);
    }
    TrackingPlugin.getDefault().getIdleDetector().deleteObserver(observer);
    PlatformUI.getWorkbench().removeWindowListener(winlistener);
  }

  @Override
  protected void doEnable() {
    checkStart();
    for (IWorkbenchWindow win : getWorkbenchWindows()) {
      win.addPerspectiveListener(persplistener);
    }
    TrackingPlugin.getDefault().getIdleDetector().addObserver(observer);
    PlatformUI.getWorkbench().addWindowListener(winlistener);
  }

  /**
   * Checks the conditions and starts recording if OK.
   */
  private void checkStart() {
    IWorkbenchWindow win = WorkbenchUtil.getActiveWindow();
    if (WorkbenchUtil.isActiveShell(win)) {
      checkStart(win);
    }
  }

  /**
   * Checks the conditions and starts recording if OK.
   * 
   * @param activeWin The current active window.
   */
  private void checkStart(@Nullable IWorkbenchWindow activeWin) {
    IPerspectiveDescriptor p = WorkbenchUtil.getPerspective(activeWin);
    if (p != null) {
      recorder.start(p);
    }
  }

  /**
   * @return All workbench windows.
   */
  private IWorkbenchWindow[] getWorkbenchWindows() {
    return PlatformUI.getWorkbench().getWorkbenchWindows();
  }

}