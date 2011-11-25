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
package rabbit.ui.internal.viewers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.junit.Before;
import org.junit.Test;

/**
 * @see CommandDescriptionProvider
 */
public class CommandDescriptionProviderTest {

  private CommandDescriptionProvider provider;

  @Before
  public void create() {
    provider = new CommandDescriptionProvider();
  }

  @Test
  public void getForegroundShouldReturnGrayForAnUndefinedCommand() {
    Command command = getCommandService().getCommand("a.b.c.e.f");
    assertThat(provider.getForeground(command), equalTo(PlatformUI
        .getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY)));
  }

  @Test
  public void getTextShouldReturnNullIfTheGivenElementIsNotACommand() {
    assertThat(provider.getText(new Object()), nullValue());
  }

  @Test
  public void getTextShouldReturnTheDescriptionOfTheCommand() throws Exception {
    Command command = getCommandWithDescription();
    assertThat(command, notNullValue());
    assertThat(provider.getText(command), equalTo(command.getDescription()));
  }

  private ICommandService getCommandService() {
    return (ICommandService) PlatformUI.getWorkbench()
        .getService(ICommandService.class);
  }

  private Command getCommandWithDescription() throws NotDefinedException {
    for (Command cmd : getCommandService().getDefinedCommands()) {
      if (cmd.getDescription() != null) {
        return cmd;
      }
    }
    return null;
  }
}
