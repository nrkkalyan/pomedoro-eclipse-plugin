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
package rabbit.data.common;

import rabbit.data.common.TaskId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @see TaskId
 */
public class TaskIdTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_dateNull() {
    new TaskId("abc", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_emptyId() {
    new TaskId("", new Date());
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_idNull() {
    new TaskId(null, new Date());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_whiteSpaceId() {
    new TaskId(" \t", new Date());
  }

  @Test
  public void testEquals() {
    String handleId = "n2u38fhcbcddf";
    Date creationDate = new Date();

    TaskId taskId1 = new TaskId(handleId, creationDate);
    TaskId taskId2 = new TaskId("rnvnh389fhvn", new Date());
    assertFalse(taskId1.equals(taskId2));

    taskId2 = new TaskId(handleId, new GregorianCalendar(1999, 1, 1).getTime());
    assertFalse(taskId1.equals(taskId2));

    taskId2 = new TaskId("nhe2834uhdfkj2938f", creationDate);
    assertFalse(taskId1.equals(taskId2));

    taskId2 = new TaskId(handleId, creationDate);
    assertTrue(taskId1.equals(taskId2));
  }

  @Test
  public void testGetCreationDate() {
    Date creationDate = new Date();
    TaskId taskId = new TaskId("anId", creationDate);
    assertEquals(creationDate, taskId.getCreationDate());
  }

  @Test
  public void testGetHandleIdentifier() {
    String handleId = "helloWorld";
    TaskId taskId = new TaskId(handleId, new Date());
    assertEquals(handleId, taskId.getHandleIdentifier());
  }

  @Test
  public void testHashCode() {
    String handleId = "ncikuhiuhfcs.sfghjowe";
    TaskId taskId = new TaskId(handleId, new Date());
    assertEquals(handleId.hashCode(), taskId.hashCode());
  }
}
