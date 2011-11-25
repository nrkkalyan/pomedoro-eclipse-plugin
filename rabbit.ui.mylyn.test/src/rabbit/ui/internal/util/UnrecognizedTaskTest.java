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

import rabbit.data.common.TaskId;
import rabbit.ui.internal.util.UnrecognizedTask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.text.DateFormat;
import java.util.Date;

/**
 * @see UnrecognizedTask
 */
public class UnrecognizedTaskTest {
  
  @Test
  public void testGetSummary() {
    TaskId id = new TaskId("abc", new Date());
    UnrecognizedTask task = new UnrecognizedTask(id);
    String expectedSummary = "Unrecognized Task. ID: " 
      + id.getHandleIdentifier() + ". Created on: " + 
      DateFormat.getDateTimeInstance().format(id.getCreationDate());
    assertEquals(expectedSummary, task.getSummary());
    
    // A task with 'no' creation date:
    id = new TaskId("123", new Date(0));
    task = new UnrecognizedTask(id);
    expectedSummary = "Unrecognized Task. ID: " + id.getHandleIdentifier();
    assertEquals(expectedSummary, task.getSummary());
  }

  @Test
  public void testEquals() {
    TaskId id1 = new TaskId("id", new Date());
    UnrecognizedTask task1 = new UnrecognizedTask(id1);
    assertFalse(task1.equals(null));
    assertTrue(task1.equals(task1));

    TaskId id2 = new TaskId("abc", new Date());
    UnrecognizedTask task2 = new UnrecognizedTask(id2);
    assertFalse(task1.equals(task2));

    id2 = new TaskId(id1.getHandleIdentifier(), new Date(id1.getCreationDate()
        .getTime() - 1000)); // 1 second difference
    task2 = new UnrecognizedTask(id2);
    assertFalse(task1.equals(task2));

    id2 = new TaskId("133444459", id1.getCreationDate());
    task2 = new UnrecognizedTask(id2);
    assertFalse(task1.equals(task2));

    id2 = new TaskId(id1.getHandleIdentifier(), id1.getCreationDate());
    task2 = new UnrecognizedTask(id2);
    assertTrue(task1.equals(task2));
  }

  @Test
  public void testGetCreationDate() {
    TaskId id = new TaskId("id", new Date());
    UnrecognizedTask task = new UnrecognizedTask(id);
    assertEquals(id.getCreationDate(), task.getCreationDate());
  }

  @Test
  public void testGetHandleIdentifier() {
    TaskId id = new TaskId("id", new Date());
    UnrecognizedTask task = new UnrecognizedTask(id);
    assertEquals(id.getHandleIdentifier(), task.getHandleIdentifier());
  }

  @Test
  public void testHashCode() {
    TaskId id = new TaskId("id", new Date());
    UnrecognizedTask task = new UnrecognizedTask(id);
    assertEquals(id.hashCode(), task.hashCode());
  }

}
