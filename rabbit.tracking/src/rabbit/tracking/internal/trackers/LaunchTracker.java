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
import rabbit.data.store.model.LaunchEvent;
import rabbit.tracking.internal.util.Recorder;

import com.google.common.collect.Maps;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.joda.time.Interval;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * Tracks launch events.
 */
public class LaunchTracker extends AbstractTracker<LaunchEvent> {

  private final IDebugEventSetListener listener = new IDebugEventSetListener() {
    @Override
    public void handleDebugEvents(DebugEvent[] events) {
      for (DebugEvent event : events) {
        handleDebugEvent(event);
      }
    }
  };

  private final Observer observer = new Observer() {
    @Override
    public void update(Observable o, Object arg) {
      if (recorders.values().contains(o)) {
        @SuppressWarnings("unchecked")
        Recorder<ILaunch> recorder = (Recorder<ILaunch>) o;
        long start = recorder.getLastRecord().getStartTimeMillis();
        long end = recorder.getLastRecord().getEndTimeMillis();
        ILaunch launch = recorder.getLastRecord().getUserData();
        ILaunchConfiguration config = launch.getLaunchConfiguration();
        if (config == null) {
          return;
        }
        ILaunchConfigurationType type = null;
        try {
          type = config.getType();
        } catch (CoreException e) {
          e.printStackTrace();
          return;
        }
        
        Set<IPath> files = launchFiles.get(launch);
        if (files == null) {
          files = Collections.emptySet();
        }
        
        Interval interval = new Interval(start, end);
        addData(new LaunchEvent(interval, launch, config, type, files));
      }
    }
  };

  /** A map of launches and the files involved (for debug launches). */
  private final Map<ILaunch, Set<IPath>> launchFiles = Maps.newHashMap();

  /** One recorder for each launch. */
  private final Map<ILaunch, Recorder<ILaunch>> recorders = Maps.newHashMap();

  /**
   * Constructs a new tracker.
   */
  public LaunchTracker() {
  }

  @Override
  protected IStorer<LaunchEvent> createDataStorer() {
    return DataHandler.getStorer(LaunchEvent.class);
  }

  @Override
  protected void doDisable() {
    DebugPlugin debug = DebugPlugin.getDefault();
    debug.removeDebugEventListener(listener);
  }

  @Override
  protected void doEnable() {
    DebugPlugin debug = DebugPlugin.getDefault();
    debug.addDebugEventListener(listener);
  }

  /**
   * Handles an event.
   * 
   * @param event The event.
   */
  private void handleDebugEvent(DebugEvent event) {
    Object source = event.getSource();

    if (source instanceof IProcess) {
      handleProcessEvent(event, (IProcess) source);

    } else if (source instanceof IThread) {
      handleThreadEvent(event, (IThread) source);
    }
  }

  /**
   * Handles the event who's source is an IProcess.
   * 
   * @param event The event.
   * @param process The process of the event.
   */
  private void handleProcessEvent(DebugEvent event, IProcess process) {
    ILaunch launch = process.getLaunch();

    // Records the start time of this launch:
    if (event.getKind() == DebugEvent.CREATE) {
      Recorder<ILaunch> r = recorders.get(launch);
      if (r == null) {
        r = new Recorder<ILaunch>();
        r.addObserver(observer);
        recorders.put(launch, r);
      }
      r.start(launch);

    } else if (event.getKind() == DebugEvent.TERMINATE) {
      Recorder<ILaunch> r = recorders.get(launch);
      if (r != null) {
        r.stop();
      }
    }
  }

  /**
   * Handles an event who's source is an IThread.
   * 
   * @param event The event.
   * @param thread The thread of the event.
   */
  private void handleThreadEvent(DebugEvent event, IThread thread) {

    // We are only interested in SUSPEND events:
    if (event.getKind() != DebugEvent.SUSPEND) {
      return;
    }

    ILaunch launch = thread.getLaunch();
    ILaunchConfiguration config = launch.getLaunchConfiguration();
    if (config == null) {
      return;
    }

    IStackFrame stack = null;
    try {
      stack = thread.getTopStackFrame();
    } catch (DebugException e) {
      return;
    }

    if (stack == null) {
      return;
    }

    ISourceLocator sourceLocator = launch.getSourceLocator();
    if (sourceLocator == null) {
      return;
    }

    Object element = sourceLocator.getSourceElement(stack);

    // Element is a file in workspace, record it:
    if (element != null && element instanceof IFile) {
      IFile file = (IFile) element;
      Set<IPath> filePaths = launchFiles.get(launch);
      if (filePaths == null) {
        filePaths = new HashSet<IPath>(3);
        launchFiles.put(launch, filePaths);
      }
      filePaths.add(file.getFullPath());
    }
  }
}
