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

import org.eclipse.ui.IPerspectiveDescriptor;
import org.joda.time.Interval;

/**
 * Represents a perspective event.
 */
public class PerspectiveEvent extends ContinuousEvent {

  private final IPerspectiveDescriptor perspective;

  /**
   * Constructs a perspective event.
   * 
   * @param interval The time interval.
   * @param perspective The perspective.
   * @throws NullPointerException If any of the arguments are null.
   */
  public PerspectiveEvent(Interval interval, IPerspectiveDescriptor perspective) {
    super(interval);
    this.perspective = checkNotNull(perspective);
  }

  /**
   * Gets the perspective.
   * 
   * @return The perspective.
   */
  public final IPerspectiveDescriptor getPerspective() {
    return perspective;
  }
}
