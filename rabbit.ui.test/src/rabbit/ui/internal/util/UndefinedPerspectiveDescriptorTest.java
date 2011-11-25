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
package rabbit.ui.internal.util;

import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test for {@link UndefinedPerspectiveDescriptor}
 */
public class UndefinedPerspectiveDescriptorTest {
  
  @Test(expected = NullPointerException.class)
  public void testConstructor_idNull() {
    new UndefinedPerspectiveDescriptor(null);
  }

  @Test
  public void testGetId() {
    String id = "adf23";
    UndefinedPerspectiveDescriptor per = new UndefinedPerspectiveDescriptor(id);
    assertEquals(id, per.getId());
  }

  @Test
  public void testGetLabel() {
    String id = "a12df23";
    UndefinedPerspectiveDescriptor per = new UndefinedPerspectiveDescriptor(id);
    assertEquals(id, per.getLabel());
  }
  
  @Test
  public void testHashCode() {
    UndefinedPerspectiveDescriptor des = new UndefinedPerspectiveDescriptor("1");
    assertEquals(des.getId().hashCode(), des.hashCode());
  }

  @Test
  public void testEquals() {
    UndefinedPerspectiveDescriptor d1 = new UndefinedPerspectiveDescriptor("1");
    UndefinedPerspectiveDescriptor d2 = new UndefinedPerspectiveDescriptor("2");
    assertFalse(d1.equals(d2));
    assertTrue(d1.equals(d1));
    assertFalse(d1.equals(null));
    
    d2 = new UndefinedPerspectiveDescriptor(d1.getId());
    assertTrue(d1.equals(d2));
  }
}
