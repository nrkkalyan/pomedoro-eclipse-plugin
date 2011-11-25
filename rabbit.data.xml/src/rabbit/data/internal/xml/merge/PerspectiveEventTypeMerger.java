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

import rabbit.data.internal.xml.schema.events.PerspectiveEventType;

/**
 * Merger for {@link PerspectiveEventType}.
 */
public class PerspectiveEventTypeMerger extends
    AbstractMerger<PerspectiveEventType> {

  public PerspectiveEventTypeMerger() {
  }
  
  @Override
  protected boolean doIsMergeable(PerspectiveEventType t1, PerspectiveEventType t2) {
    return (t1.getPerspectiveId() != null)
        && (t1.getPerspectiveId().equals(t2.getPerspectiveId()));
  }

  @Override
  protected PerspectiveEventType doMerge(PerspectiveEventType t1, PerspectiveEventType t2) {

    PerspectiveEventType result = new PerspectiveEventType();
    result.setPerspectiveId(t1.getPerspectiveId());
    result.setDuration(t1.getDuration() + t2.getDuration());
    return result;
  }

}
