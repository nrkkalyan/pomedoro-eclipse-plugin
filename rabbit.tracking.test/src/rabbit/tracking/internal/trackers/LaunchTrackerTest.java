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

import rabbit.data.store.model.LaunchEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.actions.OpenJavaPerspectiveAction;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.joda.time.Interval;
import org.junit.BeforeClass;
import org.junit.Test;

import static java.lang.String.format;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @see LaunchTracker
 */
public class LaunchTrackerTest extends AbstractTrackerTest<LaunchEvent> {

  /**
   * Listener to help testing.
   */
  private static class MyDebugListener implements IDebugEventSetListener {

    private Map<String, Long> creationTimes = new HashMap<String, Long>();
    private Map<String, Long> terminationTimes = new HashMap<String, Long>();
    private Map<String, ILaunch> launches = new HashMap<String, ILaunch>();

    @Override
    public void handleDebugEvents(DebugEvent[] events) {
      for (DebugEvent e : events) {
        if (e.getSource() instanceof IProcess) {
          IProcess process = (IProcess) e.getSource();
          ILaunch launch = process.getLaunch();
          String configName = launch.getLaunchConfiguration().getName();
          launches.put(configName, launch);

          if (e.getKind() == DebugEvent.CREATE) {
            creationTimes.put(configName, System.currentTimeMillis());

          } else if (e.getKind() == DebugEvent.TERMINATE) {
            terminationTimes.put(configName, System.currentTimeMillis());
          }
        }
      }
    }

    /**
     * Gets the launch for the given launch configuration.
     * 
     * @param launchConfigName The name of the launch configuration.
     * @return The launch, or null.
     */
    ILaunch getLaunch(String launchConfigName) {
      return launches.get(launchConfigName);
    }

    /**
     * Gets the process creation time for the given launch configuration.
     * 
     * @param launchConfigName The name of the launch configuration.
     * @return The process create time, or null.
     */
    long getProcessCreationTimeMillis(String launchConfigName) {
      return creationTimes.get(launchConfigName);
    }

    /**
     * Gets the process termination time for the given launch configuration.
     * 
     * @param launchConfigName The name of the launch configuration.
     * @return The process termination time, or null.
     */
    long getProcessTerminationTimeMillis(String launchConfigName) {
      return terminationTimes.get(launchConfigName);
    }
  }

  private static IPackageFragment pkg;

  @BeforeClass
  public static void beforeClass() throws Exception {
    new OpenJavaPerspectiveAction().run();

    // Create a new Java project:
    IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject("P");
    JavaCapabilityConfigurationPage.createProject(proj, (URI) null, null);
    IJavaProject javaProj = JavaCore.create(proj);
    JavaCapabilityConfigurationPage page = new JavaCapabilityConfigurationPage();
    page.init(javaProj, null, null, true);
    page.configureJavaProject(null);

    // Create a package:
    IPackageFragmentRoot src = javaProj.getPackageFragmentRoots()[0];
    pkg = src.createPackageFragment("pkg", true, null);
  }

  @Test
  public void testDisabled() throws Exception {
    // Create a new Java class:
    String className = "TestDisabled";
    StringBuilder content = new StringBuilder();
    content.append(format("package %s;%n", pkg.getElementName()));
    content.append(format("public class %s {%n", className));
    content
        .append(format("  public static void main(String[] args) throws Exception {%n"));
    content.append(format("    Thread.sleep(20);%n"));
    content.append(format("  }%n"));
    content.append(format("}"));
    ICompilationUnit unit = pkg.createCompilationUnit(className + ".java",
        content.toString(), true, null);

    tracker.setEnabled(false);
    launch("TestDisabled", pkg.getJavaProject().getElementName(),
        unit.getTypes()[0].getFullyQualifiedName(), ILaunchManager.DEBUG_MODE);

    assertTrue(tracker.getData().isEmpty());
  }

  /*
   * Tests the launching in normal mode ("run");
   */
  @Test
  public void testEnabled() throws CoreException, IOException,
      InterruptedException {

    // Create a new Java class:
    String className = "TestEnabled";
    StringBuilder content = new StringBuilder();
    content.append(format("package %s;%n", pkg.getElementName()));
    content.append(format("public class %s {%n", className));
    content
        .append(format("  public static void main(String[] args) throws Exception {%n"));
    content.append(format("    Thread.sleep(100);%n")); // Sleep 100 millis.
    content.append(format("  }%n"));
    content.append(format("}"));
    ICompilationUnit unit = pkg.createCompilationUnit(className + ".java",
        content.toString(), true, null);

    MyDebugListener listener = new MyDebugListener();
    DebugPlugin.getDefault().addDebugEventListener(listener);
    tracker.setEnabled(true);

    // Launch the application:
    String configName = "TestEnabled";
    ILaunchConfiguration config = launch(configName,
        pkg.getJavaProject().getElementName(),
        unit.getTypes()[0].getFullyQualifiedName(), ILaunchManager.RUN_MODE);

    ILaunch launch = null;
    final Lock lock = new ReentrantLock();
    final Condition condition = lock.newCondition();
    lock.lock();
    try {
      // Wait for listener to be notified:
      while (listener.getLaunch(configName) == null) {
        condition.await(100, TimeUnit.MILLISECONDS);
      }
      launch = listener.getLaunch(configName);
      // Wait for launch to terminate:
      while (!launch.isTerminated()) {
        condition.await(100, TimeUnit.MILLISECONDS);
      }
      // Give the tracker a bit more time to finish:
      condition.await(100, TimeUnit.MILLISECONDS);
    } finally {
      lock.unlock();
    }
    tracker.setEnabled(false);

    // Check the result:
    assertEquals(1, tracker.getData().size());
    LaunchEvent event = tracker.getData().iterator().next();
    assertTrue(event.getFilePaths().isEmpty());
    assertEquals(listener.getLaunch(configName), event.getLaunch());
    assertEquals(config, event.getLaunchConfiguration());
    long preStart = listener.getProcessCreationTimeMillis(configName) - 10;
    long start = event.getInterval().getStartMillis();
    long postStart = listener.getProcessCreationTimeMillis(configName) + 10;
    long preEnd = listener.getProcessTerminationTimeMillis(configName) - 10;
    long end = event.getInterval().getEndMillis();
    long postEnd = listener.getProcessTerminationTimeMillis(configName) + 10;
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Test
  public void testEnabled_multipleLaunches() throws Exception {
    String className1 = "TestEnabled_MultipleLaunches1";
    StringBuilder content = new StringBuilder();
    content.append(format("package %s;%n", pkg.getElementName()));
    content.append(format("public class %s {%n", className1));
    content
        .append(format("  public static void main(String[] args) throws Exception {%n"));
    content.append(format("    Thread.sleep(100);%n")); // Sleep 100 millis.
    content.append(format("  }%n"));
    content.append(format("}"));
    ICompilationUnit unit1 = pkg.createCompilationUnit(className1 + ".java",
        content.toString(), true, null);

    String className2 = "TestEnabled_MultipleLaunches2";
    content = new StringBuilder();
    content.append(format("package %s;%n", pkg.getElementName()));
    content.append(format("public class %s {%n", className2));
    content
        .append(format("  public static void main(String[] args) throws Exception {%n"));
    content.append(format("    Thread.sleep(200);%n")); // Sleep 200 millis.
    content.append(format("  }%n"));
    content.append(format("}"));
    ICompilationUnit unit2 = pkg.createCompilationUnit(className2 + ".java",
        content.toString(), true, null);

    MyDebugListener listener = new MyDebugListener();
    DebugPlugin.getDefault().addDebugEventListener(listener);
    tracker.setEnabled(true);

    // Launch two processes at the same time, running side by side:
    String configName1 = className1;
    ILaunchConfiguration config1 = launch(configName1,
        pkg.getJavaProject().getElementName(),
        unit1.getTypes()[0].getFullyQualifiedName(), ILaunchManager.RUN_MODE);
    String configName2 = className2;
    ILaunchConfiguration config2 = launch(configName2,
        pkg.getJavaProject().getElementName(),
        unit2.getTypes()[0].getFullyQualifiedName(), ILaunchManager.RUN_MODE);

    final Lock lock = new ReentrantLock();
    final Condition condition = lock.newCondition();
    lock.lock();
    try {
      while (listener.getLaunch(configName1) == null
          || listener.getLaunch(configName2) == null) {
        condition.await(100, TimeUnit.MILLISECONDS);
      }
      while (!listener.getLaunch(configName1).isTerminated()
          || !listener.getLaunch(configName2).isTerminated()) {
        condition.await(100, TimeUnit.MILLISECONDS);
      }
      condition.await(100, TimeUnit.MILLISECONDS);
    } finally {
      lock.unlock();
    }
    tracker.setEnabled(false);

    assertEquals(2, tracker.getData().size());
    LaunchEvent event1 = null;
    LaunchEvent event2 = null;
    Iterator<LaunchEvent> it = tracker.getData().iterator();
    event1 = it.next();
    event2 = it.next();
    if (event1.getLaunch().getLaunchConfiguration().getName().equals(config2)) {
      LaunchEvent tmp = event1;
      event1 = event2;
      event2 = tmp;
    }

    assertTrue(event1.getFilePaths().isEmpty());
    assertEquals(listener.getLaunch(configName1), event1.getLaunch());
    assertEquals(config1, event1.getLaunchConfiguration());
    long preStart = listener.getProcessCreationTimeMillis(configName1) - 10;
    long start = event1.getInterval().getStartMillis();
    long postStart = listener.getProcessCreationTimeMillis(configName1) + 10;
    long preEnd = listener.getProcessTerminationTimeMillis(configName1) - 10;
    long end = event1.getInterval().getEndMillis();
    long postEnd = listener.getProcessTerminationTimeMillis(configName1) + 10;
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    assertTrue(event2.getFilePaths().isEmpty());
    assertEquals(listener.getLaunch(configName2), event2.getLaunch());
    assertEquals(config2, event2.getLaunchConfiguration());
    preStart = listener.getProcessCreationTimeMillis(configName2) - 10;
    start = event2.getInterval().getStartMillis();
    postStart = listener.getProcessCreationTimeMillis(configName2) + 10;
    preEnd = listener.getProcessTerminationTimeMillis(configName2) - 10;
    end = event2.getInterval().getEndMillis();
    postEnd = listener.getProcessTerminationTimeMillis(configName2) + 10;
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Test
  public void testEnabled_withBreakpoint() throws Exception {
    String className = "TestEnabledWithBreakpoint";
    StringBuilder content = new StringBuilder();
    content.append(format("package %s;%n", pkg.getElementName()));
    content.append(format("public class %s {%n", className));
    content.append(format("  public static void main(String[] args) {%n"));
    content.append(format("    System.out.println();%n")); // This is line 4
    content.append(format("  }%n"));
    content.append(format("}"));
    ICompilationUnit unit = pkg.createCompilationUnit(className + ".java",
        content.toString(), true, null);

    // Create a breakpoint at line 4:
    JDIDebugModel.createLineBreakpoint(unit.getResource(),
        unit.getType(className).getFullyQualifiedName(), 4, -1, -1, 0, true,
        null);

    final ISuspendResume[] suspendResume = new ISuspendResume[1];
    MyDebugListener listener = new MyDebugListener() {
      @Override
      public void handleDebugEvents(DebugEvent[] events) {
        super.handleDebugEvents(events);
        for (DebugEvent e : events) {
          if (e.getKind() == DebugEvent.SUSPEND) {
            synchronized (suspendResume) {
              suspendResume[0] = (ISuspendResume) e.getSource();
              suspendResume.notifyAll();
            }
          }
        }
      }
    };
    DebugPlugin.getDefault().addDebugEventListener(listener);

    // Launch in debug mode:
    tracker.setEnabled(true);
    ILaunchConfiguration config = launch(className,
        pkg.getJavaProject().getElementName(),
        unit.getTypes()[0].getFullyQualifiedName(), ILaunchManager.DEBUG_MODE);

    synchronized (suspendResume) {
      while (suspendResume[0] == null) {
        try {
          suspendResume.wait();
        } catch (InterruptedException e) {
          // Just keep looping..
        }
      }
    }
    suspendResume[0].resume();
    Thread.sleep(500);
    tracker.setEnabled(false);

    assertEquals(1, tracker.getData().size());
    LaunchEvent event = tracker.getData().iterator().next();
    assertEquals(listener.getLaunch(className), event.getLaunch());
    assertEquals(config, event.getLaunchConfiguration());
    assertEquals(1, event.getFilePaths().size());
    assertTrue(event.getFilePaths().contains(unit.getResource().getFullPath()));
    long preStart = listener.getProcessCreationTimeMillis(className) - 10;
    long start = event.getInterval().getStartMillis();
    long postStart = listener.getProcessCreationTimeMillis(className) + 10;
    long preEnd = listener.getProcessTerminationTimeMillis(className) - 10;
    long end = event.getInterval().getEndMillis();
    long postEnd = listener.getProcessTerminationTimeMillis(className) + 10;
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Test
  public void testEnabled_withBreakpointsAndFiles() throws Exception {
    String className1 = "TestEnabledWithBreakpointsAndFiles1";
    StringBuilder content = new StringBuilder();
    content.append(format("package %s;%n", pkg.getElementName()));
    content.append(format("public class %s {%n", className1));
    content.append(format("  public %s() {%n", className1));
    content.append(format("    System.out.println();%n")); // This is line 4
    content.append(format("  }%n"));
    content.append(format("}"));
    ICompilationUnit unit1 = pkg.createCompilationUnit(className1 + ".java",
        content.toString(), true, null);

    // Create a breakpoint at line 4 in the above file:
    JDIDebugModel.createLineBreakpoint(unit1.getResource(),
        unit1.getType(className1).getFullyQualifiedName(), 4, -1, -1, 0, true,
        null);

    String className2 = "TestEnabledWithBreakpointsAndFiles2";
    content = new StringBuilder();
    content.append(format("package %s;%n", pkg.getElementName()));
    content.append(format("public class %s {%n", className2));
    content.append(format("  public static void main(String[] args) {%n"));
    content.append(format("    new %s();%n", className1)); // This is line 4
    content.append(format("  }%n"));
    content.append(format("}"));
    ICompilationUnit unit2 = pkg.createCompilationUnit(className2 + ".java",
        content.toString(), true, null);

    // Create a breakpoint at line 4 in the above file:
    JDIDebugModel.createLineBreakpoint(unit2.getResource(),
        unit2.getType(className2).getFullyQualifiedName(), 4, -1, -1, 0, true,
        null);

    final ISuspendResume[] suspendResume = new ISuspendResume[1];
    MyDebugListener listener = new MyDebugListener() {
      @Override
      public void handleDebugEvents(DebugEvent[] events) {
        super.handleDebugEvents(events);
        for (DebugEvent e : events) {
          if (e.getKind() == DebugEvent.SUSPEND) {
            suspendResume[0] = (ISuspendResume) e.getSource();
          }
        }
      }
    };
    DebugPlugin.getDefault().addDebugEventListener(listener);

    // Launch in debug mode:
    tracker.setEnabled(true);
    launch("TestEnabledWithBreakpointsAndFiles",
        pkg.getJavaProject().getElementName(),
        unit2.getType(className2).getFullyQualifiedName(),
        ILaunchManager.DEBUG_MODE);

    final Lock lock = new ReentrantLock();
    final Condition condition = lock.newCondition();
    lock.lock();
    try {
      // We had two breakpoints set, so we need to resume twice:
      while (suspendResume[0] == null) {
        condition.await(100, TimeUnit.MICROSECONDS);
      }
      suspendResume[0].resume();
      suspendResume[0] = null;
      condition.await(100, TimeUnit.MILLISECONDS);
      while (suspendResume[0] == null) {
        condition.await(100, TimeUnit.MILLISECONDS);
      }
      suspendResume[0].resume();
      condition.await(500, TimeUnit.MILLISECONDS);
    } finally {
      lock.unlock();
    }

    assertEquals(1, tracker.getData().size());
    LaunchEvent event = tracker.getData().iterator().next();
    assertEquals(2, event.getFilePaths().size());
    assertTrue(event.getFilePaths().contains(unit1.getResource().getFullPath()));
    assertTrue(event.getFilePaths().contains(unit2.getResource().getFullPath()));
  }

  @Override
  protected LaunchEvent createEvent() {
    ILaunchConfigurationType type = mock(ILaunchConfigurationType.class);
    given(type.getIdentifier()).willReturn("typeId");
    ILaunchConfiguration config = mock(ILaunchConfiguration.class);
    given(config.getName()).willReturn("name");
    ILaunch launch = mock(ILaunch.class);
    given(launch.getLaunchMode()).willReturn("run");
    return new LaunchEvent(new Interval(0, 1), launch, config, type,
        new HashSet<IPath>(Arrays.asList(new Path("/1"), new Path("/2"))));
  }

  @Override
  protected LaunchTracker createTracker() {
    return new LaunchTracker();
  }

  /**
   * Launches a Java application.
   * 
   * @param configName The name for the new launch configuration.
   * @param projectName The name of the Java project.
   * @param typeName The fully qualified name of the Java class.
   * @param mode The launch mode
   * @return The launch configuration launched.
   * @see ILaunchManager#RUN_MODE
   * @see ILaunchManager#DEBUG_MODE
   */
  private ILaunchConfiguration launch(String configName, String projectName,
      String typeName, String mode) throws CoreException {
    ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
    ILaunchConfigurationType type = manager
        .getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
    ILaunchConfigurationWorkingCopy copy = type.newInstance(null, configName);
    copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
        projectName);
    copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
        typeName);
    ILaunchConfiguration config = copy.doSave();
    config.launch(mode, null, true);
    return config;
  }
}
