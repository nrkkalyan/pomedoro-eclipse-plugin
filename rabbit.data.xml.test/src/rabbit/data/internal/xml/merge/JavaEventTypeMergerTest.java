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
package rabbit.data.internal.xml.merge;

import rabbit.data.internal.xml.merge.AbstractMerger;
import rabbit.data.internal.xml.merge.JavaEventTypeMerger;
import rabbit.data.internal.xml.schema.events.JavaEventType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @see JavaEventTypeMerger
 */
public class JavaEventTypeMergerTest extends AbstractMergerTest<JavaEventType> {

  @Override
  protected AbstractMerger<JavaEventType> createMerger() {
    return new JavaEventTypeMerger();
  }

  @Override
  protected JavaEventType createTargetType() {
    JavaEventType type = new JavaEventType();
    type.setDuration(1);
    type.setHandleIdentifier("abc");
    return type;
  }

  @Override
  protected JavaEventType createTargetTypeDiff() {
    JavaEventType type = new JavaEventType();
    type.setDuration(1000);
    type.setHandleIdentifier("19823634");
    return type;
  }

  @Override
  public void testIsMergeable() throws Exception {
    JavaEventType t1 = createTargetType();
    JavaEventType t2 = createTargetType();
    assertTrue(merger.isMergeable(t1, t2));
    
    t2.setDuration(t1.getDuration() + 1);
    assertTrue("Value is not part of the id, so this test should pass", merger.isMergeable(t1, t2));
    
    t2.setHandleIdentifier(t1.getHandleIdentifier() + "1");
    assertFalse(merger.isMergeable(t1, t2));
  }

  @Override
  public void testMerge() throws Exception {
    JavaEventType t1 = createTargetType();
    JavaEventType t2 = createTargetType();
    assertEquals(t1.getDuration() + t2.getDuration(), merger.merge(t1, t2).getDuration());
    assertEquals(t1.getHandleIdentifier(), merger.merge(t1, t2).getHandleIdentifier());
  }

  @Override
  public void testMerge_notModifyParams() throws Exception {
    String id = "abcdefg";
    long value1 = 19834;
    long value2 = 1;
    
    JavaEventType t1 = new JavaEventType();
    t1.setDuration(value1);
    t1.setHandleIdentifier(id);
    JavaEventType t2 = new JavaEventType();
    t2.setDuration(value2);
    t2.setHandleIdentifier(id);
    
    merger.merge(t1, t2);
    
    assertEquals(id, t1.getHandleIdentifier());
    assertEquals(id, t2.getHandleIdentifier());
    assertEquals(value1, t1.getDuration());
    assertEquals(value2, t2.getDuration());
  }

}
