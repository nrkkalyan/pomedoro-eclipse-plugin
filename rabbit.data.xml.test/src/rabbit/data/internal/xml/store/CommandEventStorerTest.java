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
package rabbit.data.internal.xml.store;

import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.convert.CommandEventConverter;
import rabbit.data.internal.xml.merge.CommandEventTypeMerger;
import rabbit.data.internal.xml.schema.events.CommandEventListType;
import rabbit.data.internal.xml.schema.events.CommandEventType;
import rabbit.data.store.model.CommandEvent;

import com.google.common.base.Objects;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.joda.time.DateTime;

import java.util.Collections;

/**
 * @see CommandEventStorer
 */
public class CommandEventStorerTest extends
    AbstractStorerTest<CommandEvent, CommandEventType, CommandEventListType> {

  @Override
  protected CommandEventStorer createStorer() {
    return new CommandEventStorer(new CommandEventConverter(), 
                                  new CommandEventTypeMerger(), 
                                  DataStore.COMMAND_STORE);
  }

  @Override
  protected CommandEvent createEvent(DateTime dateTime) {
    return new CommandEvent(dateTime, createExecutionEvent("adnk2o385"));
  }

  @Override
  protected CommandEvent createEventDiff(DateTime dateTime) {
    return new CommandEvent(dateTime, createExecutionEvent("23545656"));
  }

  private ExecutionEvent createExecutionEvent(String commandId) {
    return new ExecutionEvent(getCommandService().getCommand(commandId),
        Collections.EMPTY_MAP, null, null);
  }

  /**
   * Gets the workbench command service.
   */
  private ICommandService getCommandService() {
    return (ICommandService) PlatformUI.getWorkbench().getService(
        ICommandService.class);
  }

  @Override
  protected boolean equal(CommandEventType t1, CommandEventType t2) {
    return Objects.equal(t1.getCommandId(), t2.getCommandId())
        && t1.getCount() == t2.getCount();
  }
}
