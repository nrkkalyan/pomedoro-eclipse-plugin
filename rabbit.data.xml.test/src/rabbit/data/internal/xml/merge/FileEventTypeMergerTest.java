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

import rabbit.data.internal.xml.merge.FileEventTypeMerger;
import rabbit.data.internal.xml.schema.events.FileEventType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @see FileEventTypeMerger
 */
public class FileEventTypeMergerTest extends AbstractMergerTest<FileEventType> {

  /**
   * Tests when {@link FileEventType#getFilePath()} returns null on both
   * parameters,
   * {@link FileEventTypeMerger#isMergeable(FileEventType, FileEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_bothParamGetFileIdReturnsNull() {
    FileEventType t1 = new FileEventType();
    t1.setFilePath(null);
    FileEventType t2 = new FileEventType();
    t2.setFilePath(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  /**
   * Tests when {@link FileEventType#getFilePath()} returns null on the first
   * parameter, and returns not null on the second parameter,
   * {@link FileEventTypeMerger#isMergeable(FileEventType, FileEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_firstParamGetFileIdReturnsNull() {
    FileEventType t1 = new FileEventType();
    t1.setFilePath(null);
    FileEventType t2 = new FileEventType();
    t2.setFilePath("notNull");

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  /**
   * Tests when {@link FileEventType#getFilePath()} returns null on the second
   * parameter, and returns not null on the first parameter,
   * {@link FileEventTypeMerger#isMergeable(FileEventType, FileEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_secondParamGetFileIdReturnsNull() {
    FileEventType t1 = new FileEventType();
    t1.setFilePath("notNull");
    FileEventType t2 = new FileEventType();
    t2.setFilePath(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Override
  public void testIsMergeable() throws Exception {
    FileEventType t1 = createTargetType();
    FileEventType t2 = createTargetTypeDiff();

    assertTrue(merger.isMergeable(t1, t1));
    assertFalse(merger.isMergeable(t1, t2));

    t2.setFilePath(t1.getFilePath());
    assertTrue(merger.isMergeable(t1, t2));
  }

  @Override
  public void testMerge() throws Exception {
    FileEventType t1 = createTargetType();
    t1.setDuration(100);

    FileEventType t2 = createTargetTypeDiff();
    t2.setFilePath(t1.getFilePath());
    t2.setDuration(3000);

    String fileId = t1.getFilePath();
    long totalDuration = t1.getDuration() + t2.getDuration();

    FileEventType result = merger.merge(t1, t2);
    assertEquals(fileId, result.getFilePath());
    assertEquals(totalDuration, result.getDuration());
  }

  @Override
  public void testMerge_notModifyParams() throws Exception {
    String fileId = "amAnCommandId";
    int duration1 = 10010;
    int duration2 = 187341;
    
    FileEventType type1 = new FileEventType();
    type1.setFilePath(fileId);
    type1.setDuration(duration1);
    FileEventType type2 = new FileEventType();
    type2.setFilePath(fileId);
    type2.setDuration(duration2);
    
    FileEventType result = merger.merge(type1, type2);
    assertNotSame(type1, result);
    assertNotSame(type2, result);
    assertEquals(fileId, type1.getFilePath());
    assertEquals(duration1, type1.getDuration());
    assertEquals(fileId, type2.getFilePath());
    assertEquals(duration2, type2.getDuration());
  }
  
  @Override
  protected FileEventTypeMerger createMerger() {
    return new FileEventTypeMerger();
  }

  @Override
  protected FileEventType createTargetType() {
    FileEventType type = new FileEventType();
    type.setFilePath("afileId");
    type.setDuration(10);
    return type;
  }

  @Override
  protected FileEventType createTargetTypeDiff() {
    FileEventType type = new FileEventType();
    type.setFilePath("afileIdabcdefg123");
    type.setDuration(110);
    return type;
  }

}
