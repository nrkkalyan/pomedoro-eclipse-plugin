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

import rabbit.data.internal.xml.schema.events.TaskFileEventType;
import rabbit.data.internal.xml.schema.events.TaskIdType;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Merger for {@link TaskFileEventType}
 */
public class TaskFileEventTypeMerger extends AbstractMerger<TaskFileEventType> {
  
  public TaskFileEventTypeMerger() {
  }

  @Override
  protected boolean doIsMergeable(TaskFileEventType t1, TaskFileEventType t2) {
    boolean result = false;
    
    TaskIdType id1 = t1.getTaskId();
    TaskIdType id2 = t2.getTaskId();
    if (id1 != null && id2 != null) {
      
      // Check the handle identifiers of the IDs:
      result = (id1.getHandleId() != null) 
            && (id2.getHandleId() != null) 
            && (id1.getHandleId().equals(id2.getHandleId()))
            
            // Check the creation dates of the IDs:
            && (id1.getCreationDate() != null)
            && (id2.getCreationDate() != null)
            && (id1.getCreationDate().equals(id2.getCreationDate()))
            
            // Check the file IDs:
            && (t1.getFilePath() != null)
            && (t2.getFilePath() != null)
            && (t1.getFilePath().equals(t2.getFilePath()));
    }
    return result;
  }

  @Override
  protected TaskFileEventType doMerge(TaskFileEventType t1, TaskFileEventType t2) {
    TaskIdType id = new TaskIdType();
    id.setHandleId(t1.getTaskId().getHandleId());
    id.setCreationDate((XMLGregorianCalendar) t1.getTaskId().getCreationDate().clone());

    TaskFileEventType result = new TaskFileEventType();
    result.setDuration(t1.getDuration() + t2.getDuration());
    result.setFilePath(t1.getFilePath());
    result.setTaskId(id);

    return result;
  }

}
