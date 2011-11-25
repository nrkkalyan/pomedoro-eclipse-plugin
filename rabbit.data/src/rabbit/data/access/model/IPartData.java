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

import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

/**
 * Contains information regarding time spent on a workbench tool.
 * <p>
 * Values represented by the keys defined in this interface are not null.
 * </p>
 * 
 * @noimplement
 */
public interface IPartData extends IData {

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
   * Key for the workbench tool ID.
   */
  static final IKey<String> PART_ID = Keys.PART_ID;

  /**
   * Gets the workbench part that has the ID.
   * 
   * @return The workbench part, or null if no part has that ID.
   * @see #PART_ID
   */
  IWorkbenchPartDescriptor getPart();
}
