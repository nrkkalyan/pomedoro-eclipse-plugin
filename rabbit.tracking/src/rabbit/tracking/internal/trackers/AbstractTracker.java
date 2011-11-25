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
package rabbit.tracking.internal.trackers;

import rabbit.data.store.IStorer;
import rabbit.tracking.ITracker;

import org.eclipse.core.runtime.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Defines common behaviors for a tracker.
 */
public abstract class AbstractTracker<T> implements ITracker<T> {

  /** Variable to indicate whether this tracker is activated. */
  private boolean isEnabled;

  private Set<T> data;

  private IStorer<T> storer;

  /**
   * Constructs a new tracker.
   */
  public AbstractTracker() {
    isEnabled = false;
    data = new LinkedHashSet<T>();
    storer = createDataStorer();
    Assert.isNotNull(storer);
  }

  /**
   * Adds an event data to the collection.
   * 
   * @param o The data.
   */
  public void addData(T o) {
    data.add(o);
  }

  @Override
  public void flushData() {
    data.clear();
  }

  @Override
  public Collection<T> getData() {
    return Collections.unmodifiableSet(data);
  }

  @Override
  public boolean isEnabled() {
    return isEnabled;
  }

  @Override
  public void saveData() {
    if (!getData().isEmpty()) {
      storer.insert(getData());
      storer.commit();
    }
  }

  @Override
  public void setEnabled(boolean enable) {
    if (isEnabled() != enable) {
      if (enable) {
        flushData();
        doEnable();
      } else {
        doDisable();
        saveData();
      }
      isEnabled = enable;
    }
  }

  /**
   * Creates a storer for storing the data.
   * 
   * @return A data storer.
   */
  protected abstract IStorer<T> createDataStorer();

  /**
   * Disables this tracker with the necessary operations..
   * <p>
   * This method will be called by {@link #setEnabled(boolean)} if the
   * conditions are satisfied. Subclasses should override this method to disable
   * this tracker.
   * </p>
   * <p>
   * Precondition: {@link #isEnabled()} returns true.<br />
   * Postconditions: {@link #isEnabled()} returns <tt>false</tt> and this
   * tracker is disabled.
   * </p>
   * 
   * @see #setEnabled(boolean)
   */
  protected abstract void doDisable();

  /**
   * Enables this tracker with the necessary operations.
   * <p>
   * This method will be called by {@link #setEnabled(boolean)} if the
   * conditions are satisfied. Subclasses should override this method to enable
   * this tracker.
   * </p>
   * <p>
   * Precondition: {@link #isEnabled()} returns false.<br />
   * Postconditions: {@link #isEnabled()} returns <tt>true</tt> and this tracker
   * is enabled.
   * </p>
   * 
   * @see #setEnabled(boolean)
   */
  protected abstract void doEnable();
}
