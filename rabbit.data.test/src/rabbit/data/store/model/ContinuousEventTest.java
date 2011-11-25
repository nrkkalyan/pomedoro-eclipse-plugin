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

import rabbit.data.store.model.ContinuousEvent;
import rabbit.data.store.model.DiscreteEvent;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;

/**
 * @see ContinuousEvent
 */
public class ContinuousEventTest extends DiscreteEventTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_argNull() {
    Interval interval = null;
    createEvent(interval);
  }

  @Test
  public void testGetInterval() {
    Interval interval = new Interval(1, 2);
    assertEquals(interval, createEvent(interval).getInterval());
  }

  /**
   * @deprecated ContinuesEvent doesn't use an constructor with DateTime, use
   *             {@link #createEvent(Interval)} instead.
   */
  @Override
  @Deprecated
  protected final DiscreteEvent createEvent(DateTime time) {
    return super.createEvent(time);
  }

  /**
   * @see ContinuousEvent#ContinuousEvent(Interval)
   */
  protected ContinuousEvent createEvent(Interval interval) {
    return new ContinuousEvent(interval);
  }
}
