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

import org.eclipse.debug.core.model.IBreakpoint;
import org.joda.time.DateTime;

/**
 * Represents a breakpoint event.
 */
@Deprecated
public class BreakpointEvent extends DiscreteEvent {

  /**
   * Enum to indicate the status of a breakpoint.
   */
  public static enum Status {
    /** Indicates a breakpoint has been added. */
    ADDED,
    /** Indicates a breakpoint has been removed. */
    REMOVED,
    /** Indicates a breakpoint has been enabled. */
    ENABLED,
    /** Indicates a breakpoint has been disabled. */
    DISABLED;
  }

  private final IBreakpoint breakpoint;
  private final Status status;

  /**
   * Constructs a new event.
   * 
   * @param time The time of event.
   * @param breakpoint The breakpoint.
   * @param status The status of this event.
   * @throws NullPointerException If any of the arguments is null.
   */
  public BreakpointEvent(DateTime time, IBreakpoint breakpoint, Status status) {
    super(time);

    checkNotNull(breakpoint, "Breakpoint cannot be null");
    checkNotNull(status, "Status cannot be null");

    this.breakpoint = breakpoint;
    this.status = status;
  }

  /**
   * Gets the breakpoint of this event.
   * 
   * @return The breakpoint.
   */
  public IBreakpoint getBreakpoint() {
    return breakpoint;
  }

  /**
   * Gets the status of the breakpoint of this event.
   * 
   * @return The breakpoint status.
   */
  public Status getStatus() {
    return status;
  }
}
