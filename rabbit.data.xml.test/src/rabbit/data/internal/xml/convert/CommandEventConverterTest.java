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
package rabbit.data.internal.xml.convert;

import rabbit.data.internal.xml.convert.CommandEventConverter;
import rabbit.data.internal.xml.schema.events.CommandEventType;
import rabbit.data.store.model.CommandEvent;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.joda.time.DateTime;

import java.util.Collections;

/**
 * @see CommandEventConverter
 */
public class CommandEventConverterTest extends
    AbstractConverterTest<CommandEvent, CommandEventType> {

  @Override
  protected CommandEventConverter createConverter() {
    return new CommandEventConverter();
  }

  @Override
  public void testConvert() throws Exception {
    CommandEvent event = createEvent();
    CommandEventType type = converter.convert(event);
    assertEquals(event.getExecutionEvent().getCommand().getId(),
        type.getCommandId());
    assertEquals(1, type.getCount());
  }

  private CommandEvent createEvent() {
    return new CommandEvent(new DateTime(), createExecutionEvent("adnk2o385"));
  }

  private ExecutionEvent createExecutionEvent(String commandId) {
    return new ExecutionEvent(getCommandService().getCommand(commandId),
        Collections.EMPTY_MAP, null, null);
  }

  private ICommandService getCommandService() {
    return (ICommandService) PlatformUI.getWorkbench().getService(
        ICommandService.class);
  }
}
