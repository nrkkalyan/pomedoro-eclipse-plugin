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
package rabbit.data.store.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.joda.time.Interval;

import java.util.Set;

/**
 * Represents a launch event such as a debug launch.
 */
public class LaunchEvent extends ContinuousEvent {

  private final ILaunch launch;

  private final ILaunchConfiguration config;

  /** Unmodifiable set of file paths. */
  private final ImmutableSet<IPath> filePaths;

  private final ILaunchConfigurationType type;

  /*
   * Note that ILaunch.getLaunchConfiguration() and
   * ILaunchConfiguration.getType() returns the objects we want but may return
   * null, (we don't want null) therefore we specified them as non null
   * parameters rather than just taking the ILaunch.
   */

  /**
   * Constructs a new event.
   * @param interval The time interval.
   * @param config The launch configuration.
   * @param filePaths The paths of the files associated with the launch, or an
   *        empty collection.
   * @throws NullPointerException If any of the parameters are null.
   * @see IResource#getFullPath()
   */
  public LaunchEvent(Interval interval, ILaunch launch,
      ILaunchConfiguration config, ILaunchConfigurationType type,
      Set<IPath> filePaths) {
    super(interval);
    this.type = checkNotNull(type, "type");
    this.config = checkNotNull(config, "config");
    this.launch = checkNotNull(launch, "launch");
    this.filePaths = ImmutableSet.copyOf(checkNotNull(filePaths, "filePaths"));
  }

  /**
   * Gets the paths of the files involved.
   * @return an unmodifiable collection of the files involved, or an empty
   *         collection.
   */
  public final Set<IPath> getFilePaths() {
    return filePaths;
  }

  /**
   * Gets the launch.
   * @return the launch.
   */
  public final ILaunch getLaunch() {
    return launch;
  }

  /**
   * Gets the launch configuration type.
   * @return the type.
   */
  public final ILaunchConfigurationType getLaunchConfigurationType() {
    return type;
  }

  /**
   * Gets the launch configuration.
   * @return the launch configuration.
   */
  public final ILaunchConfiguration getLaunchConfiguration() {
    return config;
  }
}
