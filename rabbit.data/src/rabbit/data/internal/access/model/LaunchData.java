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
package rabbit.data.internal.access.model;

import rabbit.data.access.model.IKey;
import rabbit.data.access.model.ILaunchData;
import rabbit.data.access.model.LaunchConfigurationDescriptor;
import rabbit.data.access.model.WorkspaceStorage;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;

import org.eclipse.core.resources.IFile;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Contains launch information.
 */
public class LaunchData implements ILaunchData {
  
  /**
   * An immutable map of data.
   */
  private final Map<IKey<? extends Object>, Object> data;
  
  /**
   * Constructor.
   * @param date The date of the session.
   * @param workspace The workspace of the session.
   * @param config The launch configuration.
   * @param count The number of launches.
   * @param duration The duration of the launches.
   * @param files The files involved, or an empty collection.
   * @throws NullPointerException If any of the arguments are null.
   * @throws IllegalArgumentException If {@code count < 1};
   */
  public LaunchData(LocalDate date,
                    WorkspaceStorage workspace,
                    LaunchConfigurationDescriptor config,
                    int count,
                    Duration duration,
                    Set<IFile> files) {
    
    checkArgument(count >= 1, "count < 1");
    
    data = new KeyMapBuilder()
        .put(COUNT,  count)
        .put(DATE,          checkNotNull(date, "date"))
        .put(WORKSPACE,     checkNotNull(workspace, "workspace"))
        .put(LAUNCH_CONFIG, checkNotNull(config, "config"))
        .put(DURATION,      checkNotNull(duration, "duration"))
        .put(FILES,         ImmutableSet.copyOf(checkNotNull(files, "files")))
        .build();
        
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(@Nullable IKey<T> key) {
    return (T) data.get(key);
  }
}
