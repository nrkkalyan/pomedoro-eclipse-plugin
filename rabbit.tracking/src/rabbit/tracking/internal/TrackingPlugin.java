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

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import java.util.concurrent.TimeUnit;

/**
 * The activator class controls the plug-in life cycle
 */
public class TrackingPlugin extends AbstractUIPlugin implements
    IWorkbenchListener {

  // The plug-in ID
  public static final String PLUGIN_ID = "rabbit.tracking";

  /** ID of the tracker extension point. */
  public static final String TRACKER_EXTENSION_ID = "rabbit.tracking.trackers";

  // The shared instance
  private static TrackingPlugin plugin;

  /**
   * Returns the shared instance
   * 
   * @return the shared instance
   */
  public static TrackingPlugin getDefault() {
    return plugin;
  }

  private IdleDetector idleDetector;

  /** An set of trackers. */
  private ImmutableSet<ITracker<?>> trackers;

  /**
   * The constructor
   */
  public TrackingPlugin() {
    long oneSec = TimeUnit.SECONDS.toMillis(1);
    long oneMin = TimeUnit.MINUTES.toMillis(1);
    idleDetector = new IdleDetector(getWorkbench().getDisplay(), oneMin, oneSec);
    trackers = ImmutableSet.of();
  }

  /**
   * Gets the global idleness detector in use. Clients may attach themselves as
   * observers to the detector but must not change the detector's state (like
   * calling {@link IdleDetector#setRunning(boolean)}).
   * 
   * @return The idleness detector.
   */
  public IdleDetector getIdleDetector() {
    return idleDetector;
  }

  @Override
  public void postShutdown(IWorkbench workbench) {
    // Everything should be done before the workbench is shut down, use
    // preShutdown method instead
  }

  @Override
  public boolean preShutdown(IWorkbench workbench, boolean forced) {
    for (ITracker<?> tracker : trackers) {
      tracker.setEnabled(false);
    }
    return true;
  }

  /**
   * Call this method to saves all current data collected by the trackers now.
   * All data will be saved and flushed from the trackers.
   */
  public void saveCurrentData() {
    for (ITracker<?> tracker : trackers) {
      tracker.setEnabled(false);
      tracker.flushData();
      tracker.setEnabled(true);
    }
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;

    if (trackers != null)
      setEnableTrackers(trackers, false);

    getWorkbench().addWorkbenchListener(this);
    trackers = createTrackers();
    setEnableTrackers(trackers, true);

    idleDetector.setRunning(true);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    idleDetector.setRunning(false);
    getWorkbench().removeWorkbenchListener(this);
    setEnableTrackers(trackers, false);

    plugin = null;
    super.stop(context);
  }

  /**
   * Creates trackers from the extension point.
   * 
   * @return A list of tracker objects.
   */
  private ImmutableSet<ITracker<?>> createTrackers() {

    IConfigurationElement[] elements = Platform.getExtensionRegistry()
        .getConfigurationElementsFor(TRACKER_EXTENSION_ID);

    final ImmutableSet.Builder<ITracker<?>> builder = ImmutableSet.builder();
    for (final IConfigurationElement e : elements) {

      SafeRunner.run(new ISafeRunnable() {
        @Override
        public void handleException(Throwable e) {
          getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
          e.printStackTrace();
        }

        @Override
        public void run() throws Exception {
          Object o = e.createExecutableExtension("class");
          if (o instanceof ITracker<?>) {
            builder.add((ITracker<?>) o);
          } else {
            System.err.println("Object is not a tracker: " + o);
          }
        }

      });
    }
    return builder.build();
  }

  /**
   * Enables or disables the trackers.
   * 
   * @param trackers The trackers to perform actions on.
   * @param enable True to enable, false to disable.
   */
  private void setEnableTrackers(ImmutableCollection<ITracker<?>> trackers,
      boolean enable) {
    for (ITracker<?> tracker : trackers) {
      tracker.setEnabled(enable);
    }
  }
}
