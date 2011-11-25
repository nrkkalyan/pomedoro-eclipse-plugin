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

import org.joda.time.DateTime;

/**
 * Represents an event that happened at a particular time.
 */
public class DiscreteEvent {

  private final DateTime time;

  /**
   * Constructs a new event.
   * 
   * @param time The event time.
   * @throws NullPointerException If time is null.
   */
  public DiscreteEvent(DateTime time) {
    this.time = checkNotNull(time);
  }

  /**
   * Gets the time the event occurred.
   * 
   * @return The event time.
   */
  public final DateTime getTime() {
    return time;
  }
}
