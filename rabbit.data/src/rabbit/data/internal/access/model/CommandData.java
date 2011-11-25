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

import rabbit.data.access.model.ICommandData;
import rabbit.data.access.model.IKey;
import rabbit.data.access.model.WorkspaceStorage;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.commands.Command;
import org.joda.time.LocalDate;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Contains command execution information.
 */
public class CommandData implements ICommandData {
  
  /**
   * Immutable map of data.
   */
  private final Map<IKey<? extends Object>, Object> data;
  
  /**
   * Constructor.
   * @param date The date of the session.
   * @param workspace The workspace of the session.
   * @param command The command.
   * @param count The execution count.
   * @throws NullPointerException If any of the arguments are null;
   * @throws IllegalArgumentException If {@code count < 1}.
   */
  public CommandData(LocalDate date, 
                     WorkspaceStorage workspace, 
                     Command command, 
                     int count) {
    
    checkArgument(count >= 1, "count < 1");
    
    data = new KeyMapBuilder()
        .put(DATE,      checkNotNull(date,      "date"))
        .put(WORKSPACE, checkNotNull(workspace, "workspace"))
        .put(COMMAND,   checkNotNull(command,   "commandId"))
        .put(COUNT,     count)
        .build();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(@Nullable IKey<T> key) {
    return (T) data.get(key);
  }
}
