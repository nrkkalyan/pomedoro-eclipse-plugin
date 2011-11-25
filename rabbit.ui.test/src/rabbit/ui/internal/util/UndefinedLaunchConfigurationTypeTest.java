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

import rabbit.ui.internal.util.UndefinedLaunchConfigurationType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @see UndefinedLaunchConfigurationType
 */
public class UndefinedLaunchConfigurationTypeTest {
  
  @Test(expected = NullPointerException.class)
  public void testConstructor_argumentNull() {
    new UndefinedLaunchConfigurationType(null);
  }

  @Test
  public void testGetIdentifier() {
    String id = "hello";
    assertEquals(id, new UndefinedLaunchConfigurationType(id).getIdentifier());
  }
  
  @Test
  public void testHashCode() {
    String id = "world";
    assertEquals(id.hashCode(), new UndefinedLaunchConfigurationType(id).hashCode());
  }
  
  @Test
  public void testEquals() {
    UndefinedLaunchConfigurationType d1 = new UndefinedLaunchConfigurationType("1");
    UndefinedLaunchConfigurationType d2 = new UndefinedLaunchConfigurationType("2");
    assertFalse(d1.equals(null));
    assertFalse(d1.equals(d2));
    assertTrue(d1.equals(d1));
    
    d2 = new UndefinedLaunchConfigurationType(d1.getIdentifier());
    assertTrue(d1.equals(d2));
  }
  
  @Test
  public void testGetName() {
    String id = "hello";
    assertEquals(id, new UndefinedLaunchConfigurationType(id).getName());
  }
}

