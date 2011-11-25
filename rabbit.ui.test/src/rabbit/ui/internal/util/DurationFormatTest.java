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

import rabbit.ui.internal.util.DurationFormat;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test for {@link DurationFormat}
 */
public class DurationFormatTest {

  @Test
  public void testToDefaultString() {
    long millis = 1000;
    assertEquals("1 s", DurationFormat.format(millis));

    millis = 60000;
    assertEquals("1 min 00 s", DurationFormat.format(millis));

    millis = 3600000;
    assertEquals("1 hr 00 min 00 s", DurationFormat.format(millis));

    millis = 36061000;
    assertEquals("10 hr 01 min 01 s", DurationFormat.format(millis));
  }

}
