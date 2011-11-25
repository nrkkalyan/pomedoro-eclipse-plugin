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

import rabbit.data.internal.xml.merge.LaunchEventTypeMerger;
import rabbit.data.internal.xml.schema.events.LaunchEventType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @see LaunchEventTypeMerger
 */
public class LaunchEventTypeMergerTest extends
    AbstractMergerTest<LaunchEventType> {

  @Override
  public void testIsMergeable() throws Exception {
    LaunchEventType t1 = createTargetType();
    LaunchEventType t2 = createTargetType();
    assertTrue(merger.isMergeable(t1, t2));

    t2 = createTargetTypeDiff();
    assertFalse(merger.isMergeable(t1, t2));

    // Launch mode ID:
    t2 = createTargetType();
    assertTrue(merger.isMergeable(t1, t2));
    t2.setLaunchModeId(t2.getLaunchModeId() + ".");
    assertFalse(merger.isMergeable(t1, t2));

    // Launch type ID:
    t2 = createTargetType();
    assertTrue(merger.isMergeable(t1, t2));
    t2.setLaunchTypeId(t2.getLaunchTypeId() + ".");
    assertFalse(merger.isMergeable(t1, t2));

    // Name:
    t2 = createTargetType();
    assertTrue(merger.isMergeable(t1, t2));
    t2.setName(t2.getName() + ".");
    assertFalse(merger.isMergeable(t1, t2));
  }

  @Test
  public void testIsMergeable_bothParamGetLaunchModeIdReturnsNull() {
    LaunchEventType t1 = createTargetType();
    t1.setLaunchModeId(null);
    LaunchEventType t2 = createTargetType();
    t2.setLaunchModeId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Test
  public void testIsMergeable_bothParamGetLaunchTypeIdReturnsNull() {
    LaunchEventType t1 = createTargetType();
    t1.setLaunchTypeId(null);
    LaunchEventType t2 = createTargetType();
    t2.setLaunchTypeId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Test
  public void testIsMergeable_bothParamGetNameReturnsNull() {
    LaunchEventType t1 = createTargetType();
    t1.setName(null);
    LaunchEventType t2 = createTargetType();
    t2.setName(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Test
  public void testIsMergeable_firstParamGetLaunchModeIdReturnsNull() {
    LaunchEventType t1 = createTargetType();
    t1.setLaunchModeId(null);
    LaunchEventType t2 = createTargetType();

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Test
  public void testIsMergeable_firstParamGetLaunchTypeIdReturnsNull() {
    LaunchEventType t1 = createTargetType();
    t1.setLaunchTypeId(null);
    LaunchEventType t2 = createTargetType();

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Test
  public void testIsMergeable_firstParamGetNameReturnsNull() {
    LaunchEventType t1 = createTargetType();
    t1.setName(null);
    LaunchEventType t2 = createTargetType();

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Test
  public void testIsMergeable_secondParamGetLaunchModeIdReturnsNull() {
    LaunchEventType t1 = createTargetType();
    LaunchEventType t2 = createTargetType();
    t2.setLaunchModeId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Test
  public void testIsMergeable_secondParamGetLaunchTypeIdReturnsNull() {
    LaunchEventType t1 = createTargetType();
    LaunchEventType t2 = createTargetType();
    t2.setLaunchTypeId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Test
  public void testIsMergeable_secondParamGetNameReturnsNull() {
    LaunchEventType t1 = createTargetType();
    LaunchEventType t2 = createTargetType();
    t2.setName(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Override
  public void testMerge() throws Exception {
    LaunchEventType t1 = createTargetType();
    t1.setCount(10000);
    t1.setTotalDuration(98340);
    t1.getFilePath().addAll(Arrays.asList(System.currentTimeMillis() + ""));

    LaunchEventType t2 = createTargetType();
    t2.setCount(10000);
    t2.setTotalDuration(98340);
    t2.getFilePath().addAll(Arrays.asList(System.nanoTime() + ""));
    t2.getFilePath().addAll(t1.getFilePath());

    int totalCount = t1.getCount() + t2.getCount();
    long totalDuration = t1.getTotalDuration() + t2.getTotalDuration();
    Set<String> allFileIds = new HashSet<String>(t1.getFilePath());
    allFileIds.addAll(t2.getFilePath());

    LaunchEventType result = merger.merge(t1, t2);
    assertEquals(totalCount, result.getCount());
    assertEquals(totalDuration, result.getTotalDuration());
    assertEquals(allFileIds.size(), result.getFilePath().size());
    assertTrue(allFileIds.containsAll(result.getFilePath()));
  }
  
  @Override
  public void testMerge_notModifyParams() throws Exception {
    String name = "noName";
    String type = "abc2";
    String mode = "amAnId";
    int count1 = 238;
    int count2 = 9823;
    int duration1 = 10010;
    int duration2 = 187341;
    Set<String> fileIds1 = new HashSet<String>(Arrays.asList("a", "b"));
    Set<String> fileIds2 = new HashSet<String>(Arrays.asList("1"));
    
    LaunchEventType type1 = new LaunchEventType();
    type1.setName(name);
    type1.setLaunchTypeId(type);
    type1.setLaunchModeId(mode);
    type1.setCount(count1);
    type1.setTotalDuration(duration1);
    type1.getFilePath().addAll(fileIds1);
    LaunchEventType type2 = new LaunchEventType();
    type2.setName(name);
    type2.setLaunchTypeId(type);
    type2.setLaunchModeId(mode);
    type2.setCount(count2);
    type2.setTotalDuration(duration2);
    type2.getFilePath().addAll(fileIds2);
    
    LaunchEventType result = merger.merge(type1, type2);
    assertNotSame(type1, result);
    assertNotSame(type2, result);
    assertEquals(name, type1.getName());
    assertEquals(mode, type1.getLaunchModeId());
    assertEquals(type, type1.getLaunchTypeId());
    assertEquals(count1, type1.getCount());
    assertEquals(duration1, type1.getTotalDuration());
    assertEquals(fileIds1.size(), type1.getFilePath().size());
    assertTrue(fileIds1.containsAll(type1.getFilePath()));
    assertEquals(name, type2.getName());
    assertEquals(mode, type2.getLaunchModeId());
    assertEquals(type, type2.getLaunchTypeId());
    assertEquals(count2, type2.getCount());
    assertEquals(duration2, type2.getTotalDuration());
    assertEquals(fileIds2.size(), type2.getFilePath().size());
    assertTrue(fileIds2.containsAll(type2.getFilePath()));
  }

  @Override
  protected LaunchEventTypeMerger createMerger() {
    return new LaunchEventTypeMerger();
  }

  @Override
  protected LaunchEventType createTargetType() {
    LaunchEventType type = new LaunchEventType();
    type.setCount(1);
    type.setLaunchModeId("someModeId");
    type.setLaunchTypeId("someTypeId");
    type.setName("aName");
    type.setTotalDuration(1999);
    type.getFilePath().add("1");
    type.getFilePath().add("2");
    return type;
  }

  @Override
  protected LaunchEventType createTargetTypeDiff() {
    LaunchEventType type = new LaunchEventType();
    type.setCount(1111);
    type.setLaunchModeId("someModeId111111");
    type.setLaunchTypeId("someTypeId111111");
    type.setName("aName11111");
    type.setTotalDuration(1999111);
    type.getFilePath().add("A");
    return type;
  }

}
