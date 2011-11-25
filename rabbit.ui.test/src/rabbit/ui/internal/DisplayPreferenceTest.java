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

import rabbit.ui.Preference;
import rabbit.ui.internal.RabbitUI;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

/**
 * Test for {@link Preference}
 */
public class DisplayPreferenceTest {

  private Preference preference; // Test subject.

  @Before
  public void setUp() {
    preference = new Preference();
  }

  /** Test the start is before or equal to end time. */
  @Test
  public void testContructor() {
    assertTrue(preference.getStartDate().compareTo(preference.getEndDate()) <= 0);
    preference.getStartDate().add(Calendar.DAY_OF_MONTH,
        RabbitUI.getDefault().getDefaultDisplayDatePeriod());
    assertTrue(preference.getStartDate().compareTo(preference.getEndDate()) == 0);
  }
}
