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
package rabbit.data.store.model;

import rabbit.data.store.model.CommandEvent;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

/**
 * Test for {@link CommandEvent}
 */
public class CommandEventTest extends DiscreteEventTest {

  private ExecutionEvent exe;
  private CommandEvent event;

  @Before
  public void setUp() {
    exe = new ExecutionEvent(getCommandService().getCommand("something"),
        Collections.EMPTY_MAP, null, null);
    event = new CommandEvent(new DateTime(), exe);
  }

  @Test
  public void testCommandEvent() {
    assertNotNull(event);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_withNull() {
    new CommandEvent(new DateTime(), null);
  }

  @Test
  public void testGetExecutionEvent() {
    assertSame(exe, event.getExecutionEvent());
  }

  @Override
  protected CommandEvent createEvent(DateTime time) {
    return new CommandEvent(time, new ExecutionEvent(getCommandService()
        .getCommand("something"), Collections.EMPTY_MAP, null, null));
  }

  /**
   * Gets the workbench command service.
   * 
   * @return The command service.
   */
  private ICommandService getCommandService() {
    return (ICommandService) PlatformUI.getWorkbench().getService(
        ICommandService.class);
  }
}
