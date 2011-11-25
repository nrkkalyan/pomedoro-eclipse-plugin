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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TestUtil {

  /**
   * Creates a new tracker for testing. The trackers getData method will return
   * a modifiable collection for testing purposes.
   * 
   * @return A new tracker.
   */
  public static <T> ITracker<T> newTracker() {

    return new ITracker<T>() {

      private boolean isEnabled = false;
      private Set<T> data = new HashSet<T>();

      @Override
      public void flushData() {
        data.clear();
      }

      @Override
      public Collection<T> getData() {
        return data;
      }

      @Override
      public boolean isEnabled() {
        return isEnabled;
      }

      @Override
      public void saveData() {
      }

      @Override
      public void setEnabled(boolean enable) {
        isEnabled = enable;
      }

    };
  }

}
