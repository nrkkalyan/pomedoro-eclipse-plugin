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
package rabbit.ui.internal;

import rabbit.ui.internal.RabbitUI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.Platform;
import org.junit.Test;

/**
 * @see RabbitUI
 */
public class RabbitUITest {

  @Test
  public void testDefaultDisplayDatePeriod() {
    assertTrue(RabbitUI.getDefault().getDefaultDisplayDatePeriod() >= 0);
    RabbitUI.getDefault().setDefaultDisplayDatePeriod(10);
    assertEquals(10, RabbitUI.getDefault().getDefaultDisplayDatePeriod());
  }

  @Test
  public void testExtensionId() {
    assertTrue(Platform.getExtensionRegistry().getConfigurationElementsFor(
        RabbitUI.UI_PAGE_EXTENSION_ID).length > 0);
  }

  @Test
  public void testLoadRootElements() {
    assertNotNull(RabbitUI.getDefault().loadRootPages());
    assertFalse(RabbitUI.getDefault().loadRootPages().isEmpty());
  }

  @Test
  public void testPluginId() {
    assertEquals(RabbitUI.getDefault().getBundle().getSymbolicName(),
        RabbitUI.PLUGIN_ID);
  }

}
