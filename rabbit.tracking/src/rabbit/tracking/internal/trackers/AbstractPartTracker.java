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

import rabbit.tracking.internal.IdleDetector;
import rabbit.tracking.internal.TrackingPlugin;
import rabbit.tracking.internal.util.Recorder;
import rabbit.tracking.internal.util.WorkbenchUtil;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import java.util.Observable;
import java.util.Observer;

import javax.annotation.Nullable;

/**
 * Defines common behaviours for part trackers.
 * 
 * @param <E> The event type that is being tracked.
 */
public abstract class AbstractPartTracker<E> extends AbstractTracker<E> {

  /**
   * A recorder for recording the time.
   */
  private final Recorder<IWorkbenchPart> recorder = new Recorder<IWorkbenchPart>();

  /**
   * A part listener for tracking time spent on parts.
   */
  private final IPartListener partListener = new IPartListener() {

    @Override
    public void partActivated(IWorkbenchPart part) {
      recorder.start(part);
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part) {
    }

    @Override
    public void partClosed(IWorkbenchPart part) {
      if (part.equals(recorder.getUserData())) {
        recorder.stop();
      }
    }

    @Override
    public void partDeactivated(IWorkbenchPart part) {
      recorder.stop();
    }

    @Override
    public void partOpened(IWorkbenchPart part) {
      if (part == part.getSite().getPage().getActivePart()) {
        recorder.start(part);
      }
    }
  };

  /**
   * A window listener, starts/stops recording depending on the window state.
   */
  private final IWindowListener winListener = new IWindowListener() {

    @Override
    public void windowActivated(IWorkbenchWindow window) {
      checkStart(window.getPartService().getActivePart());
    }

    @Override
    public void windowClosed(IWorkbenchWindow window) {
      window.getPartService().removePartListener(partListener);
      recorder.stop();
    }

    @Override
    public void windowDeactivated(IWorkbenchWindow window) {
      recorder.stop();
    }

    @Override
    public void windowOpened(IWorkbenchWindow window) {
      window.getPartService().addPartListener(partListener);
      if (WorkbenchUtil.isActiveShell(window)) {
        checkStart(window.getPartService().getActivePart());
      }
    }
  };

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
          checkStart(WorkbenchUtil.getActivePart());
        } else {
          recorder.stop();
        }

      } else if (o == recorder) {
        long start = recorder.getLastRecord().getStartTimeMillis();
        long end = recorder.getLastRecord().getEndTimeMillis();
        IWorkbenchPart part = recorder.getLastRecord().getUserData();
        E event = tryCreateEvent(start, end, part);
        if (event != null) {
          addData(event);
        }
      }
    }
  };

  /**
   * Constructor.
   */
  public AbstractPartTracker() {
    super();
    recorder.addObserver(observer);
  }

  @Override
  protected void doDisable() {
    TrackingPlugin.getDefault().getIdleDetector().deleteObserver(observer);
    PlatformUI.getWorkbench().removeWindowListener(winListener);
    for (IPartService s : WorkbenchUtil.getPartServices()) {
      s.removePartListener(partListener);
    }
    recorder.stop();
  }

  @Override
  protected void doEnable() {
    TrackingPlugin.getDefault().getIdleDetector().addObserver(observer);
    PlatformUI.getWorkbench().addWindowListener(winListener);
    for (IPartService s : WorkbenchUtil.getPartServices()) {
      s.addPartListener(partListener);
    }
    IWorkbenchWindow win = WorkbenchUtil.getActiveWindow();
    if (WorkbenchUtil.isActiveShell(win)) {
      checkStart(win.getPartService().getActivePart());
    }
  }

  /**
   * Try to create an event. This method is called when a session ends.
   * 
   * @param startMillis The start time of the event in milliseconds.
   * @param endMillis The end time of the event in milliseconds.
   * @param part The workbench part of the event.
   * @return An event, or null if one should not be created.
   */
  protected abstract E tryCreateEvent(long startMillis, long endMillis,
      IWorkbenchPart part);

  /**
   * If the given part is not null, calls {@link Recorder#start(Object)} on it.
   * 
   * @param part The workbench part.
   */
  private void checkStart(@Nullable IWorkbenchPart part) {
    if (part != null) {
      recorder.start(part);
    }
  }
}
