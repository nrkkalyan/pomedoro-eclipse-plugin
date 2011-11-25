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

import org.joda.time.Interval;

/**
 * An event that has a duration.
 */
public class ContinuousEvent extends DiscreteEvent {

  private final Interval interval;

  /**
   * Constructor.
   * 
   * @param interval The interval.
   * @throws NullPointerException If parameter is null.
   */
  public ContinuousEvent(Interval interval) {
    super(checkNotNull(interval).getStart());
    this.interval = interval;
  }

  /**
   * Gets the interval of this event.
   * 
   * @return The interval.
   */
  public final Interval getInterval() {
    return interval;
  }
}
