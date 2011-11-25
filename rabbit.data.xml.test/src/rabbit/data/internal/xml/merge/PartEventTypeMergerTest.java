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
package rabbit.data.internal.xml.merge;

import rabbit.data.internal.xml.merge.PartEventTypeMerger;
import rabbit.data.internal.xml.schema.events.PartEventType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @see PartEventTypeMerger
 */
public class PartEventTypeMergerTest extends AbstractMergerTest<PartEventType> {

  /**
   * Tests when {@link PartEventType#getPartId()} returns null on both
   * parameters,
   * {@link PartEventTypeMerger#isMergeable(PartEventType, PartEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_bothParamGetPartIdReturnsNull() {
    PartEventType t1 = new PartEventType();
    t1.setPartId(null);
    PartEventType t2 = new PartEventType();
    t2.setPartId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  /**
   * Tests when {@link PartEventType#getPartId()} returns null on the first
   * parameter, and returns not null on the second parameter,
   * {@link PartEventTypeMerger#isMergeable(PartEventType, PartEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_firstParamGetPartIdReturnsNull() {
    PartEventType t1 = new PartEventType();
    t1.setPartId(null);
    PartEventType t2 = new PartEventType();
    t2.setPartId("notNull");

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  /**
   * Tests when {@link PartEventType#getPartId()} returns null on the second
   * parameter, and returns not null on the first parameter,
   * {@link PartEventTypeMerger#isMergeable(PartEventType, PartEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_secondParamGetPartIdReturnsNull() {
    PartEventType t1 = new PartEventType();
    t1.setPartId("notNull");
    PartEventType t2 = new PartEventType();
    t2.setPartId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Override
  public void testIsMergeable() throws Exception {
    PartEventType t1 = createTargetType();
    PartEventType t2 = createTargetTypeDiff();

    assertTrue(merger.isMergeable(t1, t1));
    assertFalse(merger.isMergeable(t1, t2));

    t2.setPartId(t1.getPartId());
    assertTrue(merger.isMergeable(t1, t2));
  }

  @Override
  public void testMerge() throws Exception {
    PartEventType t1 = createTargetType();
    t1.setDuration(100);

    PartEventType t2 = createTargetTypeDiff();
    t2.setPartId(t1.getPartId());
    t2.setDuration(3000);

    String partId = t1.getPartId();
    long totalDuration = t1.getDuration() + t2.getDuration();

    PartEventType result = merger.merge(t1, t2);
    assertEquals(partId, result.getPartId());
    assertEquals(totalDuration, result.getDuration());
  }
  
  @Override
  public void testMerge_notModifyParams() throws Exception {
    String partId = "amAnCommandId";
    int duration1 = 10010;
    int duration2 = 187341;
    
    PartEventType type1 = new PartEventType();
    type1.setPartId(partId);
    type1.setDuration(duration1);
    PartEventType type2 = new PartEventType();
    type2.setPartId(partId);
    type2.setDuration(duration2);
    
    PartEventType result = merger.merge(type1, type2);
    assertNotSame(type1, result);
    assertNotSame(type2, result);
    assertEquals(partId, type1.getPartId());
    assertEquals(duration1, type1.getDuration());
    assertEquals(partId, type2.getPartId());
    assertEquals(duration2, type2.getDuration());
  }

  @Override
  protected PartEventTypeMerger createMerger() {
    return new PartEventTypeMerger();
  }

  @Override
  protected PartEventType createTargetType() {
    PartEventType type = new PartEventType();
    type.setDuration(19834);
    type.setPartId("com.org.net.abc");
    return type;
  }

  @Override
  protected PartEventType createTargetTypeDiff() {
    PartEventType type = new PartEventType();
    type.setDuration(123);
    type.setPartId("apple.pear.orange");
    return type;
  }

}
