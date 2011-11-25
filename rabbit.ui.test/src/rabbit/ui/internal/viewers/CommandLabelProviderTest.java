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
package rabbit.ui.internal.viewers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.eclipse.core.commands.Command;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandImageService;
import org.eclipse.ui.commands.ICommandService;
import org.junit.Test;

import java.util.Collection;

/**
 * @see CommandLabelProvider
 */
public class CommandLabelProviderTest extends NullLabelProviderTest {

  private Command definedCmd;
  private Command undefinedCmd;

  private final ICommandService commandService = (ICommandService) PlatformUI
      .getWorkbench().getService(ICommandService.class);

  private final ICommandImageService imageService = (ICommandImageService)
      PlatformUI.getWorkbench().getService(ICommandImageService.class);

  @Test
  public void getForegroundShouldReturnGrayForAnUndefinedCommand() {
    assertThat(provider.getForeground(undefinedCmd), equalTo(PlatformUI
        .getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY)));
  }

  @Test
  public void getImageShouldReturnAnImageForACommandThatHasImage()
      throws Exception {
    @SuppressWarnings("unchecked")
    Collection<String> ids = commandService.getDefinedCommandIds();
    for (String id : ids) {
      if (imageService.getImageDescriptor(id) != null) {
        Command cmdWithImage = commandService.getCommand(id);
        assertThat(provider.getImage(cmdWithImage), notNullValue());
        break;
      }
    }
  }

  @Test
  public void getImageShouldReturnNullForACommandThatHasNoImage() {
    assertThat(provider.getImage(undefinedCmd), nullValue());
  }

  @Test
  public void getTextShouldReturnTheIdOfAnUndefinedCommand() throws Exception {
    assertThat(provider.getText(undefinedCmd), equalTo(undefinedCmd.getId()));
  }

  @Test
  public void getTextShouldReturnTheNameOfADefinedCommand() throws Exception {
    assertEquals(definedCmd.getName(), provider.getText(definedCmd));
  }

  @Override
  public void setUp() {
    super.setUp();
    provider = new CommandLabelProvider();
    definedCmd = commandService.getDefinedCommands()[0];
    undefinedCmd = commandService.getCommand(System.currentTimeMillis() + "");
  }

  @Override
  protected NullLabelProvider create() {
    return new CommandLabelProvider();
  }
}
