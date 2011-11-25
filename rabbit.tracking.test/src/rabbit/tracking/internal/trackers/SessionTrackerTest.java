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

import rabbit.data.store.model.SessionEvent;
import rabbit.tracking.internal.IdleDetector;
import rabbit.tracking.internal.TrackingPlugin;
import rabbit.tracking.internal.trackers.AbstractTracker;
import rabbit.tracking.internal.trackers.SessionTracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Interval;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

/**
 * @see SessionTracker
 */
public class SessionTrackerTest extends AbstractTrackerTest<SessionEvent> {

  @Test
  public void testDisabled() throws Exception {
    tracker.setEnabled(false);
    Thread.sleep(30);
    Shell shell = minimizeActiveShell();
    try {
      assertTrue(tracker.getData().isEmpty());
    } finally {
      shell.setMinimized(false);
    }
  }

  /**
   * Test when the tracker is set to be enabled, if there is no active workbench
   * window, no data will be recorded.
   */
  @Test
  public void testEnable_noActiveWorkbenchWindow() throws Exception {
    Shell shell = minimizeActiveShell();
    try {
      tracker.setEnabled(true);
      TimeUnit.MILLISECONDS.sleep(30);
      tracker.setEnabled(false);
      assertEquals(0, tracker.getData().size());
    } finally {
      shell.setMinimized(false);
    }
  }

  @Test
  public void testEnableThenDisable() throws Exception {
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(30);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    Collection<SessionEvent> data = tracker.getData();
    assertEquals(1, data.size());
    SessionEvent event = data.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Test
  public void testEnableThenDisable_whenNoActiveShell() throws Exception {
    Shell shell = minimizeActiveShell();
    try {
      tracker.setEnabled(true);
      tracker.flushData();
      Thread.sleep(10);
      tracker.setEnabled(false);
      assertEquals(0, tracker.getData().size());
    } finally {
      shell.setMinimized(false);
    }
  }

  @Test
  public void testIdleDetector_whenNoActiveShell() throws Exception {
    Shell shell = minimizeActiveShell();
    try {
      tracker.setEnabled(true);
      tracker.flushData();
      Thread.sleep(10);
      callIdleDetectorToNotify();
      assertEquals(0, tracker.getData().size());
    } finally {
      shell.setMinimized(false);
    }
  }

  @Test
  public void testIdleDetector_whenTrackerIsDisabled() throws Exception {
    tracker.setEnabled(false);
    tracker.flushData();
    Thread.sleep(10);
    callIdleDetectorToNotify();
    assertEquals(0, tracker.getData().size());
  }

  @Test
  public void testIdleDetector_whenTrackerIsEnabled() throws Exception {
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    assertEquals(0, tracker.getData().size());
    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    callIdleDetectorToNotify();
    long postEnd = System.currentTimeMillis();

    Collection<SessionEvent> data = tracker.getData();
    assertEquals(1, data.size());
    SessionEvent event = data.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Test
  public void testObserverIsAdded() {
    tracker.setEnabled(false); // It should remove itself from the observable
    int count = TrackingPlugin.getDefault().getIdleDetector().countObservers();
    tracker.setEnabled(true); // It should add itself to the observable
    assertEquals(count + 1,
        TrackingPlugin.getDefault().getIdleDetector().countObservers());
  }

  protected void callIdleDetectorToNotify() throws Exception {
    Field isActive = IdleDetector.class.getDeclaredField("isActive");
    isActive.setAccessible(true);

    Method setChanged = Observable.class.getDeclaredMethod("setChanged");
    setChanged.setAccessible(true);

    Method notifyObservers = Observable.class.getDeclaredMethod("notifyObservers");
    notifyObservers.setAccessible(true);

    IdleDetector detector = TrackingPlugin.getDefault().getIdleDetector();
    detector.setRunning(true);
    isActive.set(detector, false);
    setChanged.invoke(detector);
    notifyObservers.invoke(detector);
    detector.setRunning(false);
  }

  @Override
  protected SessionEvent createEvent() {
    return new SessionEvent(new Interval(10, 2000));
  }

  @Override
  protected AbstractTracker<SessionEvent> createTracker() {
    return new SessionTracker();
  }

  private Shell minimizeActiveShell() {
    Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
    shell.setMinimized(true);
    return shell;
  }
}
