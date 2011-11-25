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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.jface.viewers.TreePath;
import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link TreePathDurationConverter}.
 */
public class TreePathDurationConverterTest {

  private TreePathDurationConverter converter;

  @Before
  public void before() {
    converter = new TreePathDurationConverter();
  }

  @Test
  public void convertShouldReturnTheDurationInMillisecondsIfTheLastSegmentOfThePathIsADuration()
      throws Exception {
    long expectedMillis = 19L;
    TreePath path = new TreePath(new Object[]{"a", new Duration(expectedMillis)});
    assertThat(converter.convert(path), is(expectedMillis));
  }

  @Test
  public void convertShouldReturn0IfTheDurationElementIsNotTheLastSegmentOfThePath() {
    TreePath path = new TreePath(new Object[]{new Duration(10L), "a"});
    assertThat(converter.convert(path), is(0L));
  }

  @Test
  public void convertShouldReturn0IfThePathDoesNotContainADuration() {
    TreePath path = new TreePath(new Object[]{"a", "1"});
    assertThat(converter.convert(path), is(0L));
  }
}
