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

import rabbit.data.store.model.PerspectiveEvent;
import rabbit.tracking.internal.IdleDetector;
import rabbit.tracking.internal.TrackingPlugin;
import rabbit.tracking.internal.trackers.PerspectiveTracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Observable;
import java.util.Random;

/**
 * Test for {@link PerspectiveTracker}
 */
public class PerspectiveTrackerTest extends
    AbstractTrackerTest<PerspectiveEvent> {

  private PerspectiveTracker tracker;

  @Before
  public void setUp() {
    tracker = createTracker();

    IWorkbench wb = PlatformUI.getWorkbench();
    wb.getActiveWorkbenchWindow().getActivePage()
        .setPerspective(wb.getPerspectiveRegistry().getPerspectives()[1]);
  }

  @Test
  public void testChangePerspective() throws InterruptedException {
    IWorkbenchWindow win = getActiveWindow();
    IPerspectiveDescriptor oldPers = win.getActivePage().getPerspective();
    IPerspectiveDescriptor newPers = null;

    for (IPerspectiveDescriptor p : PlatformUI.getWorkbench()
        .getPerspectiveRegistry().getPerspectives()) {
      if (!p.equals(oldPers)) {
        newPers = p;
        break;
      }
    }

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    win.getActivePage().setPerspective(newPers);
    long postEnd = System.currentTimeMillis();

    assertEquals(1, tracker.getData().size());
    PerspectiveEvent event = tracker.getData().iterator().next();
    assertEquals(oldPers, event.getPerspective());

    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Test
  public void testClosePerspectives() throws InterruptedException {
    IWorkbenchPage page = getActiveWindow().getActivePage();
    IPerspectiveDescriptor perspective = page.getPerspective();

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    page.closeAllPerspectives(false, false);
    long postEnd = System.currentTimeMillis();

    assertEquals(1, tracker.getData().size());
    PerspectiveEvent e = tracker.getData().iterator().next();
    assertEquals(perspective, e.getPerspective());

    long start = e.getInterval().getStartMillis();
    long end = e.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Test
  public void testCloseWindow() throws Exception {
    IWorkbenchWindow win = openWindow();
    IPerspectiveDescriptor perspective = win.getActivePage().getPerspective();

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();
    Thread.sleep(20);
    long preEnd = System.currentTimeMillis();
    assertTrue(win.close());
    long postEnd = System.currentTimeMillis();

    assertEquals(1, tracker.getData().size());
    PerspectiveEvent e = tracker.getData().iterator().next();
    assertEquals(perspective, e.getPerspective());

    long start = e.getInterval().getStartMillis();
    long end = e.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Test
  public void testDisabled() throws Exception {
    tracker.setEnabled(false);

    // Test IPerspectiveListener.
    Thread.sleep(20);
    getActiveWindow().getActivePage().setPerspective(getRandomPerspective());
    assertTrue(tracker.getData().isEmpty());

    // Test IWindowListener.
    Thread.sleep(20);
    getActiveWindow().getWorkbench().openWorkbenchWindow(null);
    assertTrue(tracker.getData().isEmpty());

    // Test IdleDetector
    Thread.sleep(20);
    callIdleDetectorToNotify();
    assertTrue(tracker.getData().isEmpty());
  }

  @Test
  public void testEnable_noActiveWorkbenchWindow() throws Exception {
    for (IWorkbenchWindow win : PlatformUI.getWorkbench().getWorkbenchWindows()) {
      win.getShell().setMinimized(true);
    }

    try {
      tracker.setEnabled(true);
      Thread.sleep(50);
      tracker.setEnabled(false);
      assertTrue(tracker.getData().isEmpty());

    } finally {
      for (IWorkbenchWindow w : PlatformUI.getWorkbench().getWorkbenchWindows()) {
        w.getShell().setMinimized(false);
      }
    }
  }

  @Test
  public void testEnableThenDisable() throws InterruptedException {
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    assertEquals(1, tracker.getData().size());
    PerspectiveEvent e = tracker.getData().iterator().next();
    assertNotNull(e.getPerspective());

    long start = e.getInterval().getStartMillis();
    long end = e.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Test
  public void testIdleDetector() throws Exception {
    IPerspectiveDescriptor perspective = getActiveWindow().getActivePage()
        .getPerspective();

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    callIdleDetectorToNotify();
    long postEnd = System.currentTimeMillis();

    assertEquals(1, tracker.getData().size());
    PerspectiveEvent e = tracker.getData().iterator().next();
    assertEquals(perspective, e.getPerspective());

    long start = e.getInterval().getStartMillis();
    long end = e.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Test
  public void testNewWindow() throws Exception {
    tracker.setEnabled(true);

    long preStart = System.currentTimeMillis();
    IWorkbenchWindow window = openWindow(); // Opens a second window
    long postStart = System.currentTimeMillis();

    IPerspectiveDescriptor persp = window.getActivePage().getPerspective();
    tracker.flushData(); // Removes data from the first window

    Thread.sleep(100);

    long preEnd = System.currentTimeMillis();
    window.getActivePage().setPerspective(
        window.getWorkbench().getPerspectiveRegistry().getPerspectives()[1]);
    long postEnd = System.currentTimeMillis();

    assertEquals(1, tracker.getData().size());
    PerspectiveEvent event = tracker.getData().iterator().next();
    assertEquals(persp, event.getPerspective());

    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Test
  public void testObserverIsAdded() {
    IdleDetector dt = TrackingPlugin.getDefault().getIdleDetector();
    tracker.setEnabled(false); // It should remove itself from the observable
    int count = dt.countObservers();
    tracker.setEnabled(true); // It should add itself to the observable
    assertEquals(count + 1, dt.countObservers());
  }

  @Test
  public void testWindowDeactivated() throws Exception {
    IWorkbenchPage page = getActiveWindow().getActivePage();

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    // Open new window to cause the current window to loose focus
    page.getWorkbenchWindow().getWorkbench().openWorkbenchWindow(null);
    long postEnd = System.currentTimeMillis();

    assertEquals(1, tracker.getData().size());
    PerspectiveEvent e = tracker.getData().iterator().next();
    assertEquals(page.getPerspective(), e.getPerspective());

    long start = e.getInterval().getStartMillis();
    long end = e.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Override
  protected PerspectiveEvent createEvent() {
    return new PerspectiveEvent(new Interval(0, 1), getActiveWindow()
        .getActivePage().getPerspective());
  }

  @Override
  protected PerspectiveTracker createTracker() {
    return new PerspectiveTracker();
  }

  private void callIdleDetectorToNotify() throws Exception {
    Field isActive = IdleDetector.class.getDeclaredField("isActive");
    isActive.setAccessible(true);

    Method setChanged = Observable.class.getDeclaredMethod("setChanged");
    setChanged.setAccessible(true);

    Method notifyObservers = Observable.class
        .getDeclaredMethod("notifyObservers");
    notifyObservers.setAccessible(true);

    IdleDetector detector = TrackingPlugin.getDefault().getIdleDetector();
    detector.setRunning(true);
    isActive.set(detector, false);
    setChanged.invoke(detector);
    notifyObservers.invoke(detector);
    detector.setRunning(false);
  }

  private IWorkbenchWindow getActiveWindow() {
    return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
  }

  private IPerspectiveDescriptor getRandomPerspective() {
    IPerspectiveDescriptor[] ps = PlatformUI.getWorkbench()
        .getPerspectiveRegistry().getPerspectives();
    return ps[new Random().nextInt(ps.length)];
  }

  private IWorkbenchWindow openWindow() throws WorkbenchException {
    return PlatformUI.getWorkbench().openWorkbenchWindow(null);
  }
}
