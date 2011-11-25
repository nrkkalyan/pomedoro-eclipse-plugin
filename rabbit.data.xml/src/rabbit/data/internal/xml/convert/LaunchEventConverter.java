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
package rabbit.data.internal.xml.convert;

import rabbit.data.internal.xml.schema.events.LaunchEventType;
import rabbit.data.store.model.LaunchEvent;

import org.eclipse.core.runtime.IPath;

/**
 * Converts from {@link LaunchEvent} to {@link LaunchEventType}.
 */
public class LaunchEventConverter extends
    AbstractConverter<LaunchEvent, LaunchEventType> {

  public LaunchEventConverter() {}

  @Override
  protected LaunchEventType doConvert(LaunchEvent element) {
    LaunchEventType type = new LaunchEventType();
    for (IPath path : element.getFilePaths()) {
      type.getFilePath().add(path.toString());
    }
    type.setTotalDuration(element.getInterval().toDurationMillis());
    type.setName(element.getLaunchConfiguration().getName());
    type.setLaunchTypeId(element.getLaunchConfigurationType().getIdentifier());
    type.setLaunchModeId(element.getLaunch().getLaunchMode());
    type.setCount(1);

    return type;
  }

}
