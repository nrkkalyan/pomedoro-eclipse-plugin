/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.ui.internal.util;

import rabbit.ui.internal.util.UndefinedLaunchMode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @see UndefinedLaunchMode
 */
public class UndefinedLaunchModeTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_idNull() {
    new UndefinedLaunchMode(null);
  }
  
  @Test
  public void testGetIdentifier() {
    String id = "sfjnhuywe";
    assertEquals(id, new UndefinedLaunchMode(id).getIdentifier());
  }
  
  @Test
  public void testGetLabel() {
    String id = "hello";
    assertEquals(id, new UndefinedLaunchMode(id).getLabel());
  }
  
  @Test
  public void testGetLaunchAsLabel() {
    String id = "world";
    assertEquals(id, new UndefinedLaunchMode(id).getLaunchAsLabel());
  }
  
  @Test
  public void testHashCode() {
    String id = "sfjnhuywe";
    assertEquals(id.hashCode(), new UndefinedLaunchMode(id).hashCode());
  }
  
  @Test
  public void testEquals() {
    UndefinedLaunchMode m1 = new UndefinedLaunchMode("abc");
    UndefinedLaunchMode m2 = new UndefinedLaunchMode(m1.getIdentifier());
    assertTrue(m1.equals(m2));
    assertTrue(m1.equals(m1));
    assertFalse(m1.equals(null));
    
    m2 = new UndefinedLaunchMode(m2.getIdentifier() + "123");
    assertFalse(m1.equals(m2));
  }
}
