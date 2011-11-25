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

import rabbit.tracking.ITracker;
import rabbit.tracking.internal.TrackingPlugin;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

/**
 * Test for {@link TrackingPlugin}
 */
public class TrackingPluginTest {

  private static TrackingPlugin plugin = TrackingPlugin.getDefault();

  /**
   * Gets the trackers from the private field.
   * 
   * @param target The target to get the field from.
   * @return The trackers.
   */
  @SuppressWarnings("unchecked")
  private static Collection<ITracker<?>> getTrackers(TrackingPlugin target)
      throws Exception {
    Field f = TrackingPlugin.class.getDeclaredField("trackers");
    f.setAccessible(true);
    return (Collection<ITracker<?>>) f.get(target);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testCreateTrackers() throws Exception {
    Method createTrackers = TrackingPlugin.class
        .getDeclaredMethod("createTrackers");
    createTrackers.setAccessible(true);

    Collection<ITracker<?>> trackers = (Collection<ITracker<?>>) createTrackers
        .invoke(plugin);
    assertNotNull(trackers);
    // This may fail if one or more of the trackers (mainly our trackers)
    // are failed
    // to load:
    IConfigurationElement[] elements = Platform.getExtensionRegistry()
        .getConfigurationElementsFor(TrackingPlugin.TRACKER_EXTENSION_ID);
    assertEquals(elements.length, trackers.size());
    // if (elements.length != trackers.size()) {
    // System.err.println("WARNING: numElements=" + elements.length +
    // " numTrackers=" + trackers.size());
    // }
  }

  @Test
  public void testGetIdleDetector() {
    assertNotNull(plugin.getIdleDetector());
  }

  @Test
  public void testIdleDetectorState() {
    assertTrue(plugin.getIdleDetector().getIdleInterval() == 60000);
    assertTrue(plugin.getIdleDetector().getRunDelay() == 1000);
  }

  @Test
  public void testPluginId() {
    assertEquals(TrackingPlugin.PLUGIN_ID, plugin.getBundle().getSymbolicName());
  }

  @Test
  public void testPreShutdown() throws Exception {
    assertTrue(plugin.preShutdown(null, false));
    for (ITracker<?> tracker : getTrackers(plugin)) {
      assertFalse(tracker.isEnabled());
    }
  }

  @Test
  public void testSaveCurrentData() throws Exception {
    ITracker<Object> tracker = TestUtil.newTracker();
    tracker.getData().add(new Object());
    tracker.getData().add(new Object());
    assertFalse(tracker.getData().isEmpty());

    TrackingPlugin rc = new TrackingPlugin();
    rc.start(plugin.getBundle().getBundleContext());
    
    Field field = TrackingPlugin.class.getDeclaredField("trackers");
    field.setAccessible(true);
    field.set(rc, ImmutableSet.<ITracker<?>> of(tracker));
    
    rc.saveCurrentData();
    assertTrue(tracker.getData().isEmpty());
    rc.stop(rc.getBundle().getBundleContext());
  }

  @Test
  public void testSetEnableTrackers() throws Exception {
    Method m = TrackingPlugin.class.getDeclaredMethod("setEnableTrackers",
        ImmutableCollection.class, boolean.class);
    m.setAccessible(true);

    Set<ITracker<Object>> trackers = ImmutableSet.of(
        TestUtil.newTracker(),
        TestUtil.newTracker(),
        TestUtil.newTracker());

    // Test all trackers are disable.
    m.invoke(plugin, trackers, false);
    for (ITracker<?> tracker : trackers) {
      assertFalse(tracker.isEnabled());
    }

    // Test all trackers are enabled.
    m.invoke(plugin, trackers, true);
    for (ITracker<?> tracker : trackers) {
      assertTrue(tracker.isEnabled());
    }

    // Test all trackers are disable, again.
    m.invoke(plugin, trackers, false);
    for (ITracker<?> tracker : trackers) {
      assertFalse(tracker.isEnabled());
    }
  }

  @Test
  public void testStart() throws Exception {
    TrackingPlugin rc = new TrackingPlugin();
    rc.start(plugin.getBundle().getBundleContext());
    // It's already started by now.
    assertTrue(rc.getIdleDetector().isRunning());
    // Errors or may have loaded the wrong extension point:
    assertFalse(getTrackers(rc).isEmpty());

    for (ITracker<?> t : getTrackers(rc)) {
      assertTrue(t.toString(), t.isEnabled());
    }
    rc.stop(rc.getBundle().getBundleContext());
  }

  /**
   * Place this test at end of all tests.
   */
  @Test
  public void testStop() throws Exception {
    TrackingPlugin rc = new TrackingPlugin();
    rc.start(plugin.getBundle().getBundleContext());
    for (ITracker<?> o : getTrackers(rc)) {
      assertTrue(o.isEnabled());
    }

    rc.stop(rc.getBundle().getBundleContext());
    for (ITracker<?> o : getTrackers(rc)) {
      assertFalse(o.isEnabled());
    }
    assertFalse(rc.getIdleDetector().isRunning());
  }

  @Test
  public void testTrackerExtensionPointId() {
    assertTrue(Platform.getExtensionRegistry().getConfigurationElementsFor(
        TrackingPlugin.TRACKER_EXTENSION_ID).length > 0);
  }
}
