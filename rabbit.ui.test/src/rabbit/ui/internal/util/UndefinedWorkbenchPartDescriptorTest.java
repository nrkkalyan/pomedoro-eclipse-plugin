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

import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test for {@link UndefinedWorkbenchPartDescriptor}
 */
public class UndefinedWorkbenchPartDescriptorTest {
  
  @Test(expected = NullPointerException.class)
  public void testConstructor_idNull() {
    new UndefinedWorkbenchPartDescriptor(null);
  }

  @Test
  public void testGetId() {
    UndefinedWorkbenchPartDescriptor u = new UndefinedWorkbenchPartDescriptor("iid");
    assertEquals("iid", u.getId());
  }

  @Test
  public void testGetImage() {
    assertNotNull(new UndefinedWorkbenchPartDescriptor("id").getImageDescriptor());
  }

  @Test
  public void testGetLabel() {
    UndefinedWorkbenchPartDescriptor u = new UndefinedWorkbenchPartDescriptor("iid");
    assertNotNull(u.getLabel());
    assertEquals(u.getId(), u.getId());
  }

  @Test
  public void testHashCode() {
    UndefinedWorkbenchPartDescriptor u = new UndefinedWorkbenchPartDescriptor("1");
    assertEquals(u.getId().hashCode(), u.hashCode());
  }
  
  @Test
  public void testEquals() {
    UndefinedWorkbenchPartDescriptor d1 = new UndefinedWorkbenchPartDescriptor("1");
    UndefinedWorkbenchPartDescriptor d2 = new UndefinedWorkbenchPartDescriptor(d1.getId());
    assertTrue(d1.equals(d2));
    assertTrue(d1.equals(d1));
    assertFalse(d1.equals(null));
    
    d2 = new UndefinedWorkbenchPartDescriptor(d2.getId() + "abc");
    assertFalse(d1.equals(d2));
  }
}
