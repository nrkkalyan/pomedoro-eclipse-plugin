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

import rabbit.data.internal.xml.merge.CommandEventTypeMerger;
import rabbit.data.internal.xml.schema.events.CommandEventType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @see CommandEventTypeMerger
 */
public class CommandEventTypeMergerTest extends
    AbstractMergerTest<CommandEventType> {

  /**
   * Tests when {@link CommandEventType#getCommandId()} returns null on both
   * parameters,
   * {@link CommandEventTypeMerger#isMergeable(CommandEventType, CommandEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_bothParamGetCommandIdReturnsNull() {
    CommandEventType t1 = new CommandEventType();
    t1.setCommandId(null);
    CommandEventType t2 = new CommandEventType();
    t2.setCommandId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  /**
   * Tests when {@link CommandEventType#getCommandId()} returns null on the
   * first parameter, and returns not null on the second parameter,
   * {@link CommandEventTypeMerger#isMergeable(CommandEventType, CommandEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_firstParamGetCommandIdReturnsNull() {
    CommandEventType t1 = new CommandEventType();
    t1.setCommandId(null);
    CommandEventType t2 = new CommandEventType();
    t2.setCommandId("notNull");

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  /**
   * Tests when {@link CommandEventType#getCommandId()} returns null on the
   * second parameter, and returns not null on the first parameter,
   * {@link CommandEventTypeMerger#isMergeable(CommandEventType, CommandEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_secondParamGetCommandIdReturnsNull() {
    CommandEventType t1 = new CommandEventType();
    t1.setCommandId("notNull");
    CommandEventType t2 = new CommandEventType();
    t2.setCommandId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Override
  public void testIsMergeable() throws Exception {
    CommandEventType t1 = createTargetType();
    CommandEventType t2 = createTargetTypeDiff();

    assertTrue(merger.isMergeable(t1, t1));
    assertFalse(merger.isMergeable(t1, t2));

    t2.setCommandId(t1.getCommandId());
    assertTrue(merger.isMergeable(t1, t2));
  }

  @Override
  public void testMerge() throws Exception {
    CommandEventType t1 = createTargetType();
    t1.setCount(100);
    
    CommandEventType t2 = createTargetTypeDiff();
    t2.setCommandId(t1.getCommandId());
    t2.setCount(3000);

    String commandId = t1.getCommandId();
    int totalCount = t1.getCount() + t2.getCount();

    CommandEventType result = merger.merge(t1, t2);
    assertEquals(commandId, result.getCommandId());
    assertEquals(totalCount, result.getCount());
  }

  @Override
  public void testMerge_notModifyParams() throws Exception {
    String commandId = "amAnCommandId";
    int count1 = 10010;
    int count2 = 187341;
    
    CommandEventType type1 = new CommandEventType();
    type1.setCommandId(commandId);
    type1.setCount(count1);
    CommandEventType type2 = new CommandEventType();
    type2.setCommandId(commandId);
    type2.setCount(count2);
    
    CommandEventType result = merger.merge(type1, type2);
    assertNotSame(type1, result);
    assertNotSame(type2, result);
    assertEquals(commandId, type1.getCommandId());
    assertEquals(count1, type1.getCount());
    assertEquals(commandId, type2.getCommandId());
    assertEquals(count2, type2.getCount());
  }

  @Override
  protected CommandEventTypeMerger createMerger() {
    return new CommandEventTypeMerger();
  }

  @Override
  protected CommandEventType createTargetType() {
    CommandEventType type = new CommandEventType();
    type.setCommandId("commandIdA");
    type.setCount(1);
    return type;
  }

  @Override
  protected CommandEventType createTargetTypeDiff() {
    CommandEventType type = new CommandEventType();
    type.setCommandId("commandIdB");
    type.setCount(2);
    return type;
  }

}
