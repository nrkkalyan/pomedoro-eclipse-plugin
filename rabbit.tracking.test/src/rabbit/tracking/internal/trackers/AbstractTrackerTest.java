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

import rabbit.tracking.internal.trackers.AbstractTracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link AbstractTracker}
 */
public abstract class AbstractTrackerTest<T> {

  protected AbstractTracker<T> tracker;
  
  @Before
  public void before() {
    tracker = createTracker();
  }

  @Test
  public void testAddData() {
    tracker.addData(createEvent());
    assertEquals(1, tracker.getData().size());
  }

  @Test
  public void testFlushData() {
    tracker.addData(createEvent());
    assertFalse(tracker.getData().isEmpty());
    tracker.flushData();
    assertTrue(tracker.getData().isEmpty());
  }

  @Test
  public void testGetData() {
    assertNotNull(tracker.getData());
    assertTrue(tracker.getData().isEmpty());
  }

  @Test
  public void testIsEnabled() {
    assertFalse(tracker.isEnabled());
  }

  @Test
  public void testSaveData() {
    tracker.addData(createEvent());
    assertFalse(tracker.getData().isEmpty());
    tracker.saveData();
    assertFalse(tracker.getData().isEmpty()); // Only empty when re-enabled.
    tracker.setEnabled(false);
    tracker.setEnabled(true);
    assertTrue(tracker.getData().isEmpty());
  }

  @Test
  public void testSetEnabled() {
    if (tracker.isEnabled()) {
      tracker.setEnabled(false);
    }

    tracker.setEnabled(true);
    assertTrue(tracker.isEnabled());

    tracker.setEnabled(false);
    assertFalse(tracker.isEnabled());

    tracker.setEnabled(true);
    assertTrue(tracker.isEnabled());
  }

  @Test
  public void testTracker() {
    assertNotNull(tracker);
  }

  /**
   * Checks that this condition is true:
   * {@code
   *   preStart <= start <= postStart <= preEnd <= end <= postEnd
   * }
   */
  protected void checkTime(
    //@formatter:off
      long preStart, 
      long start, 
      long postStart, 
      long preEnd, 
      long end, 
      long postEnd) {

    assertTrue(preStart  <= start);
    assertTrue(start     <= postStart);
    assertTrue(postStart <= preEnd);
    assertTrue(preEnd    <= end);
    assertTrue(end       <= postEnd);
    //@formatter:on
  }

  /** Creates an event for testing. */
  protected abstract T createEvent();

  /** Creates a tracker for testing. */
  protected abstract AbstractTracker<T> createTracker();
}
