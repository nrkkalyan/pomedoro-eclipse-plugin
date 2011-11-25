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
package rabbit.tracking.internal.util;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Objects;

import java.util.Observable;

import javax.annotation.Nullable;

/**
 * A utility class for recording collapsed time.
 * <p>
 * This class extends {@link Observable}, and observers are notified when the
 * value of {@link #getLastRecord()} changes, the argument object passed to the
 * observers will be the new value of {@link #getLastRecord()}.
 * <p>
 * This class is thread safe.
 * 
 * @param <T>
 *          The user data type, the user data is an optional data object
 *          associate with each recording.
 */
public final class Recorder<T> extends Observable {

  /**
   * Represents a recorded session.
   */
  public static final class Record<T> {

    private final long startTimeMillis;
    private final long endTimeMillis;
    private final T userData;

    /**
     * Constructs a new record.
     * 
     * @param startMillis
     *          The start time in milliseconds.
     * @param endMillis
     *          The end time in milliseconds.
     * @param data
     *          The optional user data.
     * @throws IllegalArgumentException
     *           If {@link #endTimeMillis} < {@link #startTimeMillis}.
     */
    public Record(long startMillis, long endMillis, @Nullable T data) {
      checkArgument(endMillis >= startMillis);
      startTimeMillis = startMillis;
      endTimeMillis = endMillis;
      userData = data;
    }

    /**
     * Gets the start time of this record in milliseconds.
     * 
     * @return The start time of this record in milliseconds.
     */
    public long getStartTimeMillis() {
      return startTimeMillis;
    }

    /**
     * Gets the end time of this record in milliseconds.
     * 
     * @return The end time of this record in milliseconds.
     */
    public long getEndTimeMillis() {
      return endTimeMillis;
    }

    /**
     * Gets the associated user data.
     * 
     * @return The user data, or null if none.
     */
    public T getUserData() {
      return userData;
    }
  }

  /**
   * Start time of a recording session, in milliseconds.
   */
  private long start;

  /**
   * The associated user data for the current session.
   */
  private T data;

  private Record<T> record;

  private boolean running;

  /**
   * Constructs a new recorder.
   */
  public Recorder() {
  }

  /**
   * Gets the currently associated user data.
   * 
   * @return The user data, or null if none.
   */
  public synchronized T getUserData() {
    return data;
  }

  /**
   * Starts recording on the given user object. Calling this method when the
   * recorder is already running has no effects. If this recorder is recording
   * and the given user object is different from the one that is currently
   * referenced, {@link #stop()} will be called then a new session will be
   * started.
   * 
   * @param userData
   *          The optional user object for this record session.
   * @see #isRecording()
   */
  public synchronized void start(@Nullable T userData) {
    if (isRecording()) {
      if (!Objects.equal(data, userData)) {
        stop();
      } else {
        return;
      }
    }
    start = System.currentTimeMillis();
    data = userData;
    running = true;
  }

  /**
   * Starts recording, same as calling start(null). Calling this method when the
   * recorder is already running has no effects.
   * 
   * @see #isRecording()
   */
  public synchronized void start() {
    start(null);
  }

  /**
   * Stops recording. Calling this method when the recorder is not running has
   * no effects.
   */
  public void stop() {
    Record<T> r = null;
    synchronized (this) {
      if (!isRecording()) {
        return;
      }
      record = new Record<T>(start, System.currentTimeMillis(), data);
      r = record;
      running = false;
      data = null;
    }
    setChanged();
    notifyObservers(r);
  }

  /**
   * Indicates whether this recorder is currently recording.
   * 
   * @return True if this recorder is currently recording a session, false
   *         otherwise.
   */
  public synchronized boolean isRecording() {
    return running;
  }

  /**
   * Gets the last recorded session. Observers of this class will be notified
   * when the return value of this method changes.
   * 
   * @return The last recorded session.
   */
  public synchronized Record<T> getLastRecord() {
    return record;
  }

}
