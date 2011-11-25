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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.jface.viewers.TreePath;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link TreePathIntConverter}.
 */
public class TreePathIntConverterTest {

  private TreePathIntConverter converter;

  @Before
  public void before() {
    converter = new TreePathIntConverter();
  }

  @Test
  public void convertShouldReturnTheIntegerIfTheLastSegmentOfThePathIsAnInteger()
      throws Exception {
    Integer expectedInt = Integer.valueOf(19);
    TreePath path = new TreePath(new Object[]{"a", expectedInt});
    assertThat(converter.convert(path), equalTo(expectedInt.longValue()));
  }

  @Test
  public void convertShouldReturn0IfTheIntegerIsNotTheLastSegmentOfThePath() {
    TreePath path = new TreePath(new Object[]{Integer.valueOf(19), "a"});
    assertThat(converter.convert(path), equalTo(0L));
  }

  @Test
  public void convertShouldReturn0IfThePathDoesNotContainAnInteger() {
    TreePath path = new TreePath(new Object[]{"a", "1"});
    assertThat(converter.convert(path), is(0L));
  }
}
