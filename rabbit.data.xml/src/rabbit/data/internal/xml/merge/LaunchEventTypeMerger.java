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

import rabbit.data.internal.xml.schema.events.LaunchEventType;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Merger for {@link LaunchEventType}.
 */
public class LaunchEventTypeMerger extends AbstractMerger<LaunchEventType> {
  
  public LaunchEventTypeMerger() {
  }

  @Override
  protected boolean doIsMergeable(LaunchEventType t1, LaunchEventType t2) {
    return (t1.getName() != null)
        && (t1.getName().equals(t2.getName()))
        
        && (t1.getLaunchModeId() != null)
        && (t1.getLaunchModeId().equals(t2.getLaunchModeId()))
        
        && (t1.getLaunchTypeId() != null)
        && (t1.getLaunchTypeId().equals(t2.getLaunchTypeId()));
  }

  @Override
  protected LaunchEventType doMerge(LaunchEventType t1, LaunchEventType t2) {
    LaunchEventType result = new LaunchEventType();
    result.setLaunchModeId(t1.getLaunchModeId());
    result.setLaunchTypeId(t1.getLaunchTypeId());
    result.setName(t1.getName());
    result.setCount(t1.getCount() + t2.getCount());
    result.setTotalDuration(t1.getTotalDuration() + t2.getTotalDuration());

    Set<String> fileIds = Sets.newLinkedHashSet(t1.getFilePath());
    fileIds.addAll(t2.getFilePath());
    result.getFilePath().addAll(fileIds);
    return result;
  }

}
