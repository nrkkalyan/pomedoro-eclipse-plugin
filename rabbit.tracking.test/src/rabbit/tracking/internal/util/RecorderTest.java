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

import rabbit.tracking.internal.util.Recorder;
import rabbit.tracking.internal.util.Recorder.Record;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Observable;
import java.util.Observer;

/**
 * @see Recorder
 */
public class RecorderTest {

  /**
   * Observer, the internal count increments by 1 each time
   * {@link Observer#update(Observable, Object)} is called.
   */
  private static final class ObserverMock implements Observer {

    private int count = 0;
    private Object object = null;

    @Override
    public void update(Observable o, Object arg) {
      count++;
      object = arg;
    }

    /**
     * Gets the argument that was passed to {@link #update(Observable, Object)}.
     */
    Object arg() {
      return object;
    }

    /**
     * Gets the count. The count increments by 1 each time
     * {@link Observer#update(Observable, Object)} is called.
     */
    int count() {
      return count;
    }

  }

  private Recorder<Object> recorder;

  @Before
  public void before() {
    recorder = new Recorder<Object>();
  }

  @Test
  public void test_normal() {
    long start = System.currentTimeMillis();
    Object obj = new int[2];
    recorder.start(obj);
    recorder.stop();
    long end = System.currentTimeMillis();
    check(recorder.getLastRecord(), start, end, obj);
  }

  @Test
  public void testGetRecord_notNull() {
    recorder.start(null);
    recorder.stop();
    assertNotNull(recorder.getLastRecord());
  }

  @Test
  public void testGetRecord_null() {
    assertNull(recorder.getLastRecord());
  }

  @Test
  public void testGetUserData_notNull() {
    recorder.start(this);
    assertEquals(this, recorder.getUserData());
  }

  @Test
  public void testGetUserData_null() {
    assertNull(recorder.getUserData());
    recorder.start();
    assertNull(recorder.getUserData());
  }

  @Test
  public void testIsRecording_false() {
    assertFalse(recorder.isRecording());
  }

  @Test
  public void testIsRecording_true() {
    recorder.start(null);
    assertTrue(recorder.isRecording());
  }

  @Test
  public void testObservable() throws Exception {
    ObserverMock obs = new ObserverMock();
    recorder.addObserver(obs);
    long start = System.currentTimeMillis();
    recorder.start(this);
    Thread.sleep(100);
    recorder.stop();
    long end = System.currentTimeMillis();
    assertEquals(1, obs.count());
    assertNotNull(obs.arg());
    assertTrue(obs.arg() instanceof Record<?>);
    check((Record<?>) obs.arg(), start, end, this);
  }

  @Test
  public void testObservable_startMultiCalls() {
    ObserverMock obs = new ObserverMock();
    recorder.addObserver(obs);
    recorder.start();
    recorder.start();
    recorder.start();
    assertEquals(0, obs.count());
  }

  @Test
  public void testObservable_startStartObject_firstRecord() {
    ObserverMock obs = new ObserverMock();
    recorder.addObserver(obs);
    long start = System.currentTimeMillis();
    recorder.start(null);
    recorder.start("abc");
    long end = System.currentTimeMillis();
    assertEquals(1, obs.count());
    assertNotNull(obs.arg());
    assertTrue(obs.arg() instanceof Record<?>);
    check((Record<?>) obs.arg(), start, end, null);
  }

  @Test
  public void testObservable_startStartObject_secondRecord() {
    ObserverMock obs = new ObserverMock();
    recorder.addObserver(obs);
    recorder.start();
    long start = System.currentTimeMillis();
    recorder.start(this);
    recorder.stop();
    long end = System.currentTimeMillis();
    assertEquals(2, obs.count());
    assertNotNull(obs.arg());
    assertTrue(obs.arg() instanceof Record<?>);
    check((Record<?>) obs.arg(), start, end, this);
  }

  @Test
  public void testObservable_stopMultiCalls() {
    ObserverMock obs = new ObserverMock();
    recorder.addObserver(obs);
    recorder.stop();
    recorder.stop();
    recorder.stop();
    recorder.stop();
    assertEquals(0, obs.count());
  }

  @Test
  public void testStart_multiCalls() throws Exception {
    long start = System.currentTimeMillis();
    recorder.start();
    Thread.sleep(100);
    recorder.start();
    recorder.start();
    recorder.stop();
    assertTrue(start <= recorder.getLastRecord().getStartTimeMillis());
  }

  @Test
  public void testStart_nullArg() {
    recorder.start(null);
  }

  @Test
  public void testStart_startOnDiffData_firstRecord() {
    long start = System.currentTimeMillis();
    Object obj = this;
    recorder.start(obj);
    recorder.start(null);
    long end = System.currentTimeMillis();
    check(recorder.getLastRecord(), start, end, obj);
  }

  @Test
  public void testStart_startOnDiffData_secondRecord() {
    Object obj = this;
    recorder.start(null);
    long start = System.currentTimeMillis();
    recorder.start(obj);
    recorder.stop();
    long end = System.currentTimeMillis();
    check(recorder.getLastRecord(), start, end, obj);
  }

  @Test
  public void testStop_multiCalls() throws Exception {
    recorder.start();
    long beforeEnd = System.currentTimeMillis();
    recorder.stop();
    long afterEnd = System.currentTimeMillis();
    Thread.sleep(100);
    recorder.stop();
    recorder.stop();
    assertTrue(beforeEnd <= recorder.getLastRecord().getEndTimeMillis());
    assertTrue(afterEnd >= recorder.getLastRecord().getEndTimeMillis());
  }

  /**
   * Checks the given record again the given parameters to see if the record is
   * acceptable.
   * 
   * @param record The record to check.
   * @param start The start time in milliseconds, just before a recording is
   *          started.
   * @param end The end time in milliseconds, just after a recording is stopped.
   * @param data The expected user data of {@link #recorder}
   */
  private void check(Record<?> record, long start, long end, Object data) {
    assertNotNull(record);
    assertEquals(data, record.getUserData());
    assertTrue(start <= record.getStartTimeMillis());
    assertTrue(start + 10 >= record.getStartTimeMillis());
    assertTrue(end >= record.getEndTimeMillis());
    assertTrue(end - 10 <= record.getEndTimeMillis());
  }
}
