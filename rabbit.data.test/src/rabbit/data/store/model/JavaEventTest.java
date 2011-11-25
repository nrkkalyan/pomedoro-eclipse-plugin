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
package rabbit.data.store.model;

import static org.junit.Assert.assertEquals;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.joda.time.Interval;
import org.junit.Test;

/**
 * @see JavaEvent
 */
public class JavaEventTest extends ContinuousEventTest {
  
  @Test(expected = NullPointerException.class)
  public void testConstructor_javaElementNull() {
    createEvent(new Interval(0, 1), null);
  }
  
  @Test
  public void testGetJavaElement() {
    IJavaElement element = JavaCore.create("=Enfo/src<enfo{EnfoPlugin.java");
    assertEquals(element, createEvent(new Interval(0, 1), element).getElement());
  }

  @Override
  protected final ContinuousEvent createEvent(Interval interval) {
    return createEvent(interval, JavaCore.create("=Enfo/src<enfo{EnfoPlugin.java"));
  }
  
  /**
   * @see JavaEvent#JavaEvent(Interval, IJavaElement)
   */
  protected JavaEvent createEvent(Interval interval, IJavaElement element) {
    return new JavaEvent(interval, element);
  }
}
