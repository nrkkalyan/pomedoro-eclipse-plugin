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
package rabbit.data.access.model;

import org.eclipse.core.resources.IFile;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.Set;

/**
 * Contains launch information.
 * <p>
 * Values represented by the keys defined in this interface are not null.
 * </p>
 * 
 * @noimplement
 */
public interface ILaunchData extends IData {

  /**
   * Key for the date.
   */
  static final IKey<LocalDate> DATE = Keys.DATE;

  /**
   * Key for the workspace.
   */
  static final IKey<WorkspaceStorage> WORKSPACE = Keys.WORKSPACE;

  /**
   * Key for the duration.
   */
  static final IKey<Duration> DURATION = Keys.DURATION;

  /**
   * Key for the launch count.
   */
  static final IKey<Integer> COUNT = Keys.COUNT;

  /**
   * Key for the files involved.
   */
  static final IKey<Set<IFile>> FILES = Keys.FILES;

  /**
   * Key for the launch configuration.
   */
  static final IKey<LaunchConfigurationDescriptor> LAUNCH_CONFIG = Keys.LAUNCH_CONFIG;
}
