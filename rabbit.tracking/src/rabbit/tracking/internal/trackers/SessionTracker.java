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
import rabbit.data.store.model.SessionEvent;
import rabbit.tracking.internal.IdleDetector;
import rabbit.tracking.internal.TrackingPlugin;
import rabbit.tracking.internal.util.Recorder;
import rabbit.tracking.internal.util.Recorder.Record;
import rabbit.tracking.internal.util.WorkbenchUtil;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Interval;

import java.util.Observable;
import java.util.Observer;

/**
 * Tracks duration of Eclipse sessions.
 */
public class SessionTracker extends AbstractTracker<SessionEvent> {

  private final Recorder<Object> recorder = new Recorder<Object>();

  private final Observer observer = new Observer() {
    @Override
    public void update(Observable o, Object arg) {
      if (!isEnabled()) {
        return;
      }

      if (o == TrackingPlugin.getDefault().getIdleDetector()) {
        IdleDetector dt = (IdleDetector) o;
        if (dt.isUserActive()) {
          PlatformUI.getWorkbench().getDisplay().syncExec(startRecorder);
        } else {
          recorder.stop();
        }

      } else if (o == recorder) {
        Record<Object> r = recorder.getLastRecord();
        long start = r.getStartTimeMillis();
        long end = r.getEndTimeMillis();
        addData(new SessionEvent(new Interval(start, end)));
      }
    }
  };

  private final Runnable startRecorder = new Runnable() {
    @Override
    public void run() {
      // Check for active shell instead of active workbench window to include
      // dialogs (!shell.getMinimized() is also important, why?):
      IWorkbenchWindow win = WorkbenchUtil.getActiveWindow();
      if (WorkbenchUtil.isActiveShell(win)) {
        recorder.start();
      }
    }
  };

  /**
   * Constructor.
   */
  public SessionTracker() {
    recorder.addObserver(observer);
  }

  @Override
  protected IStorer<SessionEvent> createDataStorer() {
    return DataHandler.getStorer(SessionEvent.class);
  }

  @Override
  protected void doDisable() {
    TrackingPlugin.getDefault().getIdleDetector().deleteObserver(observer);
    recorder.stop();
  }

  @Override
  protected void doEnable() {
    TrackingPlugin.getDefault().getIdleDetector().addObserver(observer);
    PlatformUI.getWorkbench().getDisplay().syncExec(startRecorder);
  }
}
