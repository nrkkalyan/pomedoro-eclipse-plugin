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

import rabbit.data.internal.xml.DatatypeUtil;
import rabbit.data.internal.xml.merge.AbstractMerger;
import rabbit.data.internal.xml.merge.TaskFileEventTypeMerger;
import rabbit.data.internal.xml.schema.events.TaskFileEventType;
import rabbit.data.internal.xml.schema.events.TaskIdType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.joda.time.LocalDate;
import org.junit.Test;

/**
 * @see TaskFileEventType
 */
public class TaskFileEventTypeMergerTest extends 
    AbstractMergerTest<TaskFileEventType> {
  
  @Override
  public void testIsMergeable() throws Exception {
   TaskFileEventType t1 = createTargetType();
   TaskFileEventType t2 = createTargetType();
   assertTrue(merger.isMergeable(t1, t2));
   
   // Change the duration has no affect:
   t2.setDuration(t2.getDuration() + 1);
   assertTrue(merger.isMergeable(t1, t2));
   
   t2.setFilePath(t2.getFilePath() + "1");
   assertFalse(merger.isMergeable(t1, t2));
   t2.setFilePath(t1.getFilePath());
   assertTrue(merger.isMergeable(t1, t2));
   
   TaskIdType id2 = t2.getTaskId();
   id2.getCreationDate().setYear(id2.getCreationDate().getYear() + 1);
   assertFalse(merger.isMergeable(t1, t2));
   id2.getCreationDate().setYear(t1.getTaskId().getCreationDate().getYear());
   assertTrue(merger.isMergeable(t1, t2));
   
   id2.setHandleId(id2.getHandleId() + "1");
   assertFalse(merger.isMergeable(t1, t2));
  }

  @Test
  public void testIsMergeable_bothParamGetFileIdReturnsNull() {
    TaskFileEventType t1 = createTargetType();
    t1.setFilePath(null);
    TaskFileEventType t2 = createTargetType();
    t2.setFilePath(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("All null, not mergable, should return false instead of exception");
    }
  }

  @Test
  public void testIsMergeable_bothParamGetTaskIdGetCreationDateReturnsNull() {
    TaskFileEventType t1 = createTargetType();
    t1.getTaskId().setCreationDate(null);
    TaskFileEventType t2 = createTargetType();
    t2.getTaskId().setCreationDate(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("All null, not mergable, should return false instead of exception");
    }
  }
  
  @Test
  public void testIsMergeable_bothParamGetTaskIdGetHandleIdReturnsNull() {
    TaskFileEventType t1 = createTargetType();
    t1.getTaskId().setHandleId(null);
    TaskFileEventType t2 = createTargetType();
    t2.getTaskId().setHandleId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("All null, not mergable, should return false instead of exception");
    }}
  
  @Test
  public void testIsMergeable_bothParamGetTaskIdReturnsNull() {
    TaskFileEventType t1 = createTargetType();
    t1.setTaskId(null);
    TaskFileEventType t2 = createTargetType();
    t2.setTaskId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("All null, not mergable, should return false instead of exception");
    }
  }
  
  @Test
  public void testIsMergeable_firstParamGetFileIdReturnsNull() {
    TaskFileEventType t1 = createTargetType();
    t1.setFilePath(null);
    TaskFileEventType t2 = createTargetType();
    t2.setFilePath("notNull");

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }
  
  @Test
  public void testIsMergeable_firstParamGetTaskIdGetCreationDateReturnsNull() {
    TaskFileEventType t1 = createTargetType();
    t1.getTaskId().setCreationDate(null);
    TaskFileEventType t2 = createTargetType();

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }
  
  @Test
  public void testIsMergeable_firstParamGetTaskIdGetHandleIdReturnsNull() {
    TaskFileEventType t1 = createTargetType();
    t1.getTaskId().setHandleId(null);
    TaskFileEventType t2 = createTargetType();

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }
  
  @Test
  public void testIsMergeable_firstParamGetTaskIdReturnsNull() {
    TaskFileEventType t1 = createTargetType();
    t1.setTaskId(null);
    TaskFileEventType t2 = createTargetType();

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }
  
  @Test
  public void testIsMergeable_secondParamGetFileIdReturnsNull() {
    TaskFileEventType t1 = createTargetType();
    t1.setFilePath("notNull");
    TaskFileEventType t2 = createTargetType();
    t2.setFilePath(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }
  
  @Test
  public void testIsMergeable_secondParamGetTaskIdGetCreationDateReturnsNull() {
    TaskFileEventType t1 = createTargetType();
    TaskFileEventType t2 = createTargetType();
    t2.getTaskId().setCreationDate(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Test
  public void testIsMergeable_secondParamGetTaskIdGetHandleIdReturnsNull() {
    TaskFileEventType t1 = createTargetType();
    TaskFileEventType t2 = createTargetType();
    t2.getTaskId().setHandleId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }}

  @Test
  public void testIsMergeable_secondParamGetTaskIdReturnsNull() {
    TaskFileEventType t1 = createTargetType();
    TaskFileEventType t2 = createTargetType();
    t2.setTaskId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Override
  public void testMerge() throws Exception {
    long duration1 = 34;
    long duration2 = 2398;
    String fileId = "124";
    String handleId = "abc";
    LocalDate date = new LocalDate(1999, 1, 1);
    
    TaskIdType id1 = new TaskIdType();
    id1.setHandleId(handleId);
    id1.setCreationDate(DatatypeUtil.toXmlDate(date));
    
    TaskIdType id2 = new TaskIdType();
    id2.setHandleId(handleId);
    id2.setCreationDate(DatatypeUtil.toXmlDate(date));
    
    TaskFileEventType t1 = new TaskFileEventType();
    t1.setFilePath(fileId);
    t1.setDuration(duration1);
    t1.setTaskId(id1);
    
    TaskFileEventType t2 = new TaskFileEventType();
    t2.setFilePath(fileId);
    t2.setDuration(duration2);
    t2.setTaskId(id2);
    
    // Check that the objects created are mergeable, if this line fails, doesn't
    // mean the test has failed, just that we need to change our code to make
    // sure the two objects are mergeable, for the purpose of this test:
    assertTrue(merger.isMergeable(t1, t2));
    
    // Now the real tests:
    TaskFileEventType merged = merger.merge(t1, t2);
    assertEquals(duration1 + duration2, merged.getDuration());
    assertEquals(fileId, merged.getFilePath());
    assertNotSame(t1.getTaskId(), merged.getTaskId());
    assertNotSame(t2.getTaskId(), merged.getTaskId());
    assertEquals(handleId, merged.getTaskId().getHandleId());
    assertEquals(DatatypeUtil.toXmlDate(date), merged.getTaskId().getCreationDate());
  }

  @Override
  public void testMerge_notModifyParams() throws Exception {
    long duration1 = 34;
    long duration2 = 2398;
    String fileId = "124";
    String handleId = "abc";
    LocalDate date = new LocalDate(1999, 1, 1);
    
    TaskIdType id1 = new TaskIdType();
    id1.setHandleId(handleId);
    id1.setCreationDate(DatatypeUtil.toXmlDate(date));
    
    TaskIdType id2 = new TaskIdType();
    id2.setHandleId(handleId);
    id2.setCreationDate(DatatypeUtil.toXmlDate(date));
    
    TaskFileEventType t1 = new TaskFileEventType();
    t1.setFilePath(fileId);
    t1.setDuration(duration1);
    t1.setTaskId(id1);
    
    TaskFileEventType t2 = new TaskFileEventType();
    t2.setFilePath(fileId);
    t2.setDuration(duration2);
    t2.setTaskId(id2);
    
    // Check that the objects created are mergeable, if this line fails, doesn't
    // mean the test has failed, just that we need to change our code to make
    // sure the two objects are mergeable, for the purpose of this test:
    assertTrue(merger.isMergeable(t1, t2));
    
    // Now the real tests, call the method first:
    TaskFileEventType merged = merger.merge(t1, t2);
    assertNotSame(t1.getTaskId(), merged.getTaskId());
    assertNotSame(t2.getTaskId(), merged.getTaskId());
    
    // Check the parameters are not altered:
    assertEquals(duration1, t1.getDuration());
    assertEquals(fileId, t1.getFilePath());
    assertSame(id1, t1.getTaskId());
    assertEquals(handleId, t1.getTaskId().getHandleId());
    assertEquals(DatatypeUtil.toXmlDate(date), t1.getTaskId().getCreationDate());

    assertEquals(duration2, t2.getDuration());
    assertEquals(fileId, t2.getFilePath());
    assertSame(id2, t2.getTaskId());
    assertEquals(handleId, t2.getTaskId().getHandleId());
    assertEquals(DatatypeUtil.toXmlDate(date), t2.getTaskId().getCreationDate());
  }
  
  @Test
  public void testMerger_clonesDate() {
    TaskFileEventType t1 = createTargetType();
    TaskFileEventType t2 = createTargetType();
    TaskFileEventType merged = merger.merge(t1, t2);
    assertNotSame(merged.getTaskId().getCreationDate(), t1.getTaskId().getCreationDate());
    assertNotSame(merged.getTaskId().getCreationDate(), t2.getTaskId().getCreationDate());
  }

  @Override
  protected AbstractMerger<TaskFileEventType> createMerger() {
    return new TaskFileEventTypeMerger();
  }

  @Override
  protected TaskFileEventType createTargetType() {
    TaskIdType id = new TaskIdType();
    id.setCreationDate(DatatypeUtil.toXmlDate(new LocalDate()));
    id.setHandleId("aTaskId");
    
    TaskFileEventType type = new TaskFileEventType();
    type.setDuration(129);
    type.setFilePath("helloWorld");
    type.setTaskId(id);
    
    return type;
  }

  @Override
  protected TaskFileEventType createTargetTypeDiff() {
    TaskIdType id = new TaskIdType();
    id.setCreationDate(DatatypeUtil.toXmlDate(new LocalDate(1999, 1, 1)));
    id.setHandleId("1.2.3.4.5.6.7");
    
    TaskFileEventType type = new TaskFileEventType();
    type.setDuration(12119);
    type.setFilePath("0.9.8.7.6.");
    type.setTaskId(id);
    
    return type;
  }

}
