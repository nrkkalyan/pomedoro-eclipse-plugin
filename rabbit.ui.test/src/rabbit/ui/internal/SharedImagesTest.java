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

import rabbit.ui.internal.SharedImages;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import java.lang.reflect.Field;

/**
 * @see SharedImages
 */
public class SharedImagesTest {

  @Test
  public void testAllFields() throws Exception {
    Field[] fields = SharedImages.class.getFields();
    for (Field field : fields)
      assertNotNull(field.getName(), field.get(null));
  }
}
