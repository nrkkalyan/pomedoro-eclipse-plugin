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

import rabbit.data.internal.xml.merge.PerspectiveEventTypeMerger;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @see PerspectiveEventTypeMerger
 */
public class PerspectiveEventTypeMergerTest extends
    AbstractMergerTest<PerspectiveEventType> {

  /**
   * Tests when {@link PerspectiveEventType#getPerspectiveId()} returns null on
   * both parameters,
   * {@link PerspectiveEventTypeMerger#isMergeable(PerspectiveEventType, PerspectiveEventType)}
   * should return false (as not mergeable) instead of failing.
   */
  @Test
  public void testIsMerageable_bothParamGetPerspectiveIdReturnsNull() {
    PerspectiveEventType t1 = new PerspectiveEventType();
    t1.setPerspectiveId(null);
    PerspectiveEventType t2 = new PerspectiveEventType();
    t2.setPerspectiveId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  /**
   * Tests when {@link PerspectiveEventType#getPerspectiveId()} returns null on
   * the first parameter, and returns not null on the second parameter,
   * {@link PerspectiveEventTypeMerger#isMergeable(PerspectiveEventType, PerspectiveEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_firstParamGetPerspectiveIdReturnsNull() {
    PerspectiveEventType t1 = new PerspectiveEventType();
    t1.setPerspectiveId(null);
    PerspectiveEventType t2 = new PerspectiveEventType();
    t2.setPerspectiveId("NotNull");

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  /**
   * Tests when {@link PerspectiveEventType#getPerspectiveId()} returns null on
   * the second parameter, and returns not null on the first parameter,
   * {@link PerspectiveEventTypeMerger#isMergeable(PerspectiveEventType, PerspectiveEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_secondParamGetPerspectiveIdReturnsNull() {
    PerspectiveEventType t1 = new PerspectiveEventType();
    t1.setPerspectiveId("NotNull");
    PerspectiveEventType t2 = new PerspectiveEventType();
    t2.setPerspectiveId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Override
  protected PerspectiveEventTypeMerger createMerger() {
    return new PerspectiveEventTypeMerger();
  }

  @Override
  protected PerspectiveEventType createTargetType() {
    PerspectiveEventType type = new PerspectiveEventType();
    type.setDuration(19834);
    type.setPerspectiveId("abc.1234");
    return type;
  }

  @Override
  protected PerspectiveEventType createTargetTypeDiff() {
    PerspectiveEventType type = new PerspectiveEventType();
    type.setDuration(98);
    type.setPerspectiveId("1234567890");
    return type;
  }

  @Override
  public void testIsMergeable() throws Exception {
    PerspectiveEventType t1 = createTargetType();
    PerspectiveEventType t2 = createTargetTypeDiff();

    assertTrue(merger.isMergeable(t1, t1));
    assertFalse(merger.isMergeable(t1, t2));

    t2.setPerspectiveId(t1.getPerspectiveId());
    assertTrue(merger.isMergeable(t1, t2));
  }

  @Override
  public void testMerge() throws Exception {
    PerspectiveEventType t1 = createTargetType();
    t1.setDuration(100);

    PerspectiveEventType t2 = createTargetTypeDiff();
    t2.setPerspectiveId(t1.getPerspectiveId());
    t2.setDuration(3000);

    String perspectiveId = t1.getPerspectiveId();
    long totalDuration = t1.getDuration() + t2.getDuration();

    PerspectiveEventType result = merger.merge(t1, t2);
    assertEquals(perspectiveId, result.getPerspectiveId());
    assertEquals(totalDuration, result.getDuration());
  }
  
  @Override
  public void testMerge_notModifyParams() throws Exception {
    String perspectiveId = "amAnCommandId";
    int duration1 = 10010;
    int duration2 = 187341;
    
    PerspectiveEventType type1 = new PerspectiveEventType();
    type1.setPerspectiveId(perspectiveId);
    type1.setDuration(duration1);
    PerspectiveEventType type2 = new PerspectiveEventType();
    type2.setPerspectiveId(perspectiveId);
    type2.setDuration(duration2);
    
    PerspectiveEventType result = merger.merge(type1, type2);
    assertNotSame(type1, result);
    assertNotSame(type2, result);
    assertEquals(perspectiveId, type1.getPerspectiveId());
    assertEquals(duration1, type1.getDuration());
    assertEquals(perspectiveId, type2.getPerspectiveId());
    assertEquals(duration2, type2.getDuration());
  }

}
