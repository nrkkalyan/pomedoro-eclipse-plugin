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
package rabbit.tracking;

import java.util.Collection;

/**
 * Represents a tracker that tracks events.
 */
public interface ITracker<T> {

  /**
   * Flushes the data collected by this tracker.
   */
  public void flushData();

  /**
   * Gets the data collected by this tracker.
   * 
   * @return The data.
   */
  public Collection<T> getData();

  /**
   * Checks whether this tracker is enabled.
   * 
   * @return <tt>true</tt> if this tracker is enabled, <tt>false</tt> otherwise.
   */
  public boolean isEnabled();

  /**
   * Saves the data collected by this tracker.
   */
  public void saveData();

  /**
   * Enables or disables this tracker. When disabled, this tracker will not
   * track any events, and previous data will be saved. When enabled, all
   * previous data will be flushed.
   * 
   * @param enable <tt>true</tt> to enable this tracker, <tt>false</tt> to
   *          disable this tracker. Calling this method will have no effect if
   *          <tt>(enable == {@link #isEnabled()})</tt>.
   */
  public void setEnabled(boolean enable);

}
