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

import rabbit.data.store.model.ContinuousEvent;
import rabbit.tracking.internal.IdleDetector;
import rabbit.tracking.internal.TrackingPlugin;
import rabbit.tracking.internal.trackers.AbstractPartTracker;
import rabbit.tracking.internal.util.WorkbenchUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.ui.actions.OpenJavaBrowsingPerspectiveAction;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.FileEditorInput;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Observable;

/**
 * Test {@link AbstractPartTracker}
 */
public abstract class AbstractPartTrackerTest<E extends ContinuousEvent>
    extends AbstractTrackerTest<E> {

  protected AbstractPartTracker<E> tracker;

  @BeforeClass
  public static void setUpBeforeClass() {
    new OpenJavaBrowsingPerspectiveAction().run();
  }

  @Before
  public void setup() {
    tracker = createTracker();
  }

  @Test
  public void testChangeEditor() throws Exception {
    IEditorPart editor = openNewEditor();

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    openNewEditor();
    long postEnd = System.currentTimeMillis();

    assertEquals(1, tracker.getData().size());
    E event = tracker.getData().iterator().next();
    assertTrue(hasSamePart(event, editor));

    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Test
  public void testCloseEditor() throws Exception {
    IEditorPart editor = openNewEditor();

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    assertTrue(tracker.getData().isEmpty());
    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    editor.getEditorSite().getPage().closeEditor(editor, false);
    long postEnd = System.currentTimeMillis();

    assertEquals(1, tracker.getData().size());
    E event = tracker.getData().iterator().next();
    assertTrue(hasSamePart(event, editor));

    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Test
  public void testCloseWindow() throws Exception {
    IWorkbenchWindow win = openNewWindow();
    IEditorPart editor = openNewEditor();

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    assertTrue(tracker.getData().isEmpty());
    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    win.close();
    long postEnd = System.currentTimeMillis();

    assertEquals(1, tracker.getData().size());
    E event = tracker.getData().iterator().next();
    assertTrue(hasSamePart(event, editor));

    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Test
  public void testDisabled() throws Exception {
    tracker.setEnabled(false);

    // Test IPerspectiveListener.
    Thread.sleep(30);
    openNewEditor();

    assertTrue(tracker.getData().isEmpty());

    // Test IWindowListener.
    Thread.sleep(20);
    IWorkbenchWindow win = openNewWindow();
    assertTrue(tracker.getData().isEmpty());
    win.close();

    // Test IdleDetector
    Thread.sleep(20);
    callIdleDetectorToNotify();
    assertTrue(tracker.getData().isEmpty());
  }

  /**
   * Test when the tracker is set to be enabled, if there is no active workbench
   * window, no data will be recorded.
   */
  @Test
  public void testEnable_noActiveWorkbenchWindow() throws Exception {
    IWorkbenchWindow win = WorkbenchUtil.getActiveWindow();
    win.getShell().setMinimized(true);

    try {
      tracker.setEnabled(true);
      Thread.sleep(20);
      tracker.setEnabled(false);
      assertEquals(0, tracker.getData().size());

    } finally {
      win.getShell().setMinimized(false);
    }
  }

  @Test
  public void testEnableThenDisable() throws Exception {
    IEditorPart editor = openNewEditor();

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    assertEquals(1, tracker.getData().size());
    E event = tracker.getData().iterator().next();
    assertTrue(hasSamePart(event, editor));

    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Test
  public void testIdleDetector() throws Exception {
    IEditorPart editor = openNewEditor();

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    callIdleDetectorToNotify();
    long postEnd = System.currentTimeMillis();

    assertEquals(1, tracker.getData().size());
    E event = tracker.getData().iterator().next();
    assertTrue(hasSamePart(event, editor));

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
    IEditorPart editor = openNewEditor();

    IWorkbenchWindow win = null;
    try {
      long preStart = System.currentTimeMillis();
      tracker.setEnabled(true);
      long postStart = System.currentTimeMillis();

      assertEquals(0, tracker.getData().size());
      Thread.sleep(30);

      long preEnd = System.currentTimeMillis();
      win = openNewWindow();
      long postEnd = System.currentTimeMillis();

      assertEquals(1, tracker.getData().size());
      E event = tracker.getData().iterator().next();
      assertTrue(hasSamePart(event, editor));

      long start = event.getInterval().getStartMillis();
      long end = event.getInterval().getEndMillis();
      checkTime(preStart, start, postStart, preEnd, end, postEnd);

    } finally {
      if (win != null) {
        win.close();
      }
    }
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
  protected abstract E createEvent();

  @Override
  protected abstract AbstractPartTracker<E> createTracker();

  /**
   * Gets a file for testing.
   * 
   * @return A test file.
   */
  protected IFile getFileForTesting() throws CoreException,
      FileNotFoundException, IOException {

    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IProject project = root.getProject("Tmp");
    if (!project.exists()) {
      project.create(null);
    }
    if (!project.isOpen()) {
      project.open(null);
    }

    IFile file = project.getFile(System.nanoTime() + ".txt");
    if (!file.exists()) {
      File tmpFile = File.createTempFile("tmp", "txt");
      FileInputStream stream = new FileInputStream(tmpFile);
      file.create(stream, false, null);
      stream.close();
    }
    return file;
  }

  /**
   * Test that the event is recorded for the given part.
   * 
   * @param event The event.
   * @param part The part expected.
   * @return True if the event is recorded for the given part, false if the
   *         event is recorded for a different part.
   */
  protected abstract boolean hasSamePart(E event, IWorkbenchPart part);

  /**
   * Opens a new editor.
   * 
   * @return The newly opened editor.
   */
  protected IEditorPart openNewEditor() throws Exception {
    IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(
        "a.txt");
    return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
        new FileEditorInput(getFileForTesting()), desc.getId(), true);
  }

  /**
   * Opens a new window.
   * 
   * @return The newly opened window.
   */
  protected IWorkbenchWindow openNewWindow() throws WorkbenchException {
    return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getWorkbench().openWorkbenchWindow(
        null);
  }
}
