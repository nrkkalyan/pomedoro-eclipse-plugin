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
package rabbit.tracking.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import java.util.Observable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for detecting user idleness.
 * 
 * <p>
 * When a user enters an inactive state from an active state, or when the user
 * enters an active state from an inactive state, the observers will be
 * notified, then the observers can call {@link #isUserActive()} to check the
 * current user state.
 * </p>
 * <p>
 * When {@link #isRunning()} is false, no observers will be notified.
 * </p>
 */
public final class IdleDetector extends Observable implements Listener {

  private ScheduledThreadPoolExecutor timer;
  private ScheduledFuture<?> currentTask;
  private final Display display;
  private boolean isRunning;
  private boolean isActive;
  private long lastEventNanoTime;
  private final long idleIntervalMillis;
  private final long runDelayMillis;

  private final Runnable taskCode = new Runnable() {
    @Override
    public void run() {
      if (!isActive) {
        return;
      }
      long durationMillis = TimeUnit.NANOSECONDS.toMillis(nowNanoTime()
          - lastEventNanoTime);
      if (durationMillis > idleIntervalMillis) {
        isActive = false;
        setChanged();
        notifyObservers();
      }
    }
  };

  private final Runnable addFilters = new Runnable() {
    @Override
    public void run() {
      display.addFilter(SWT.KeyDown, IdleDetector.this);
      display.addFilter(SWT.MouseDown, IdleDetector.this);
    }
  };

  private final Runnable removeFilters = new Runnable() {
    @Override
    public void run() {
      display.removeFilter(SWT.KeyDown, IdleDetector.this);
      display.removeFilter(SWT.MouseDown, IdleDetector.this);
    }
  };

  /**
   * Constructor. When constructed, this object is not yet running.
   * 
   * @param disp
   *          The display to listen to.
   * @param idleTime
   *          After no activities within this period (in milliseconds), the user
   *          is considered idle.
   * @param delay
   *          The time (in milliseconds) of how often checks should run.
   * @throws NullPointerException
   *           If display is null.
   * @throws IllegalArgumentException
   *           If the interval or the delay is negative.
   * @see #setRunning(boolean)
   */
  public IdleDetector(Display disp, long idleTime, long delay) {
    if (disp == null) {
      throw new NullPointerException();
    }
    if (idleTime < 0 || delay < 0) {
      throw new IllegalArgumentException();
    }
    isRunning = false;
    isActive = false;
    runDelayMillis = delay;
    idleIntervalMillis = idleTime;
    display = disp;
  }

  /**
   * Gets the display of this detector.
   * 
   * @return The display, never null.
   */
  public Display getDisplay() {
    return display;
  }

  public long getIdleInterval() {
    return idleIntervalMillis;
  }

  public long getRunDelay() {
    return runDelayMillis;
  }

  @Override
  public void handleEvent(Event event) {
    
    synchronized (this) {
      lastEventNanoTime = nowNanoTime();
      if (isActive) {
        return;
      }
      isActive = true;
    }
    
    setChanged();
    notifyObservers();
  }

  /**
   * Checks whether this detector is running.
   * 
   * @return True if running, false otherwise.
   */
  public synchronized boolean isRunning() {
    return isRunning;
  }

  /**
   * Checks whether this user is active.
   * 
   * @return True if the user is active, false otherwise. <br />
   *         If this idle detector is not running, this method will always
   *         return true.
   * @see #isRunning()
   */
  public synchronized boolean isUserActive() {
    if (!isRunning) {
      return true;
    }
    return isActive;
  }

  /**
   * Sets whether this object should be running or not. Subsequence calls to set
   * the same state will have no effects. If the display is disposed, call this
   * method has no effects.
   * 
   * @param run
   *          True to run, false to stop.
   */
  public synchronized void setRunning(boolean run) {
    if (isRunning == run || display.isDisposed()) {
      return;
    }

    if (run) {
      isRunning = true;
      isActive = true;
      lastEventNanoTime = nowNanoTime();
      display.syncExec(addFilters);
      timer = new ScheduledThreadPoolExecutor(1);
      currentTask = timer.scheduleWithFixedDelay(taskCode, runDelayMillis,
          runDelayMillis, TimeUnit.MILLISECONDS);
    } else {
      display.syncExec(removeFilters);
      currentTask.cancel(false);
      isRunning = false;
      isActive = false;
      timer.shutdownNow();
    }
  }

  /**
   * Gets the current time in nanoseconds, using {@link System#nanoTime()}.
   * 
   * @return The current time in nanoseconds.
   */
  private long nowNanoTime() {
    return System.nanoTime();
  }
}
