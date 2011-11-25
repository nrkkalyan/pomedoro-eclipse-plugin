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

import rabbit.tracking.internal.util.Recorder.Record;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @see Record
 */
public class RecordTest {

  @Test
  public void testConstructor_nullUserData() {
    new Record<Object>(0, 1, null); // No exception.
  }

  @Test(expected = IllegalArgumentException.class)
  public void testContructor_endLessThanStart() {
    new Record<Object>(1, 0, "");
  }

  @Test
  public void testContructor_equalStartAndEnd() {
    new Record<Object>(1, 1, ""); // No exception.
  }

  @Test
  public void testGetEndTimeMillis() {
    assertEquals(8, new Record<Object>(7, 8, null).getEndTimeMillis());
  }

  @Test
  public void testGetStartTimeMillis() {
    assertEquals(7, new Record<Object>(7, 8, null).getStartTimeMillis());
  }

  @Test
  public void testGetUserData() {
    assertEquals(this, new Record<Object>(0, 1, this).getUserData());
  }
}
