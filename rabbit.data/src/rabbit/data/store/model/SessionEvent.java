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

import org.joda.time.Interval;

/**
 * Represents a duration spent using Eclipse.
 */
public class SessionEvent extends ContinuousEvent {

  /**
   * Constructs a new event.
   * 
   * @param interval The time interval.
   * @throws NullPointerException If time is null.
   */
  public SessionEvent(Interval interval) {
    super(interval);
  }
}
