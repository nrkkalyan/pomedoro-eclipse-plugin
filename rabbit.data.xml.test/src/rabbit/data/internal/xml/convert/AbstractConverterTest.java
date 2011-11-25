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
package rabbit.data.internal.xml.convert;

import rabbit.data.internal.xml.convert.AbstractConverter;

import org.junit.Test;

/**
 * @see AbstractConverter
 */
public abstract class AbstractConverterTest<F, T> {

  protected AbstractConverter<F, T> converter = createConverter();

  @Test(expected = NullPointerException.class)
  public void testConvert_paramNull() {
    converter.convert(null);
  }

  /**
   * Tests the converter returns what the subclass desires.
   */
  @Test
  public abstract void testConvert() throws Exception;

  /**
   * Creates a converter for testing.
   */
  protected abstract AbstractConverter<F, T> createConverter();

}
