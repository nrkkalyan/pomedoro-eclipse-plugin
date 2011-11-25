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

import rabbit.data.internal.xml.schema.events.SessionEventType;

/**
 * Merges {@link SessionEventType} elements.
 */
public class SessionEventTypeMerger extends AbstractMerger<SessionEventType> {
  
  public SessionEventTypeMerger() {
  }

  @Override
  protected boolean doIsMergeable(SessionEventType t1, SessionEventType t2) {
    // All session events are mergeable, because they contain no ID, only value.
    return true;
  }

  @Override
  protected SessionEventType doMerge(SessionEventType t1, SessionEventType t2) {
    SessionEventType type = new SessionEventType();
    type.setDuration(t1.getDuration() + t2.getDuration());
    return type;
  }

}
