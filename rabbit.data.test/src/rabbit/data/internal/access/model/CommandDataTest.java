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
import rabbit.data.access.model.WorkspaceStorage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.Path;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;

/**
 * @see CommandData
 */
public class CommandDataTest {

  /** Default value is > 1. */
  private int count;
  private LocalDate date;
  private WorkspaceStorage workspace;
  private Command command;

  @Before
  public void before() throws Exception {
    Constructor<Command> c = Command.class.getDeclaredConstructor(String.class);
    c.setAccessible(true);
    command = c.newInstance("id");
    date = new LocalDate();
    workspace = new WorkspaceStorage(new Path(""), new Path(""));
    count = 10;
  }

  @Test
  public void shouldReturnNullIfKeyIsNull() {
    assertThat(create(date, workspace, command, 1).get(null), is(nullValue()));
  }

  @Test
  public void shouldAcceptIfConstructedWithCountOfOne() {
    create(date, workspace, command, 1);
  }

  @Test
  public void shouldReturnTheCommand() {
    assertThat(create(date, workspace, command, count)
        .get(ICommandData.COMMAND), is(command));
  }

  @Test
  public void shouldReturnTheCount() {
    assertThat(
        create(date, workspace, command, count).get(ICommandData.COUNT),
        is(count));
  }

  @Test
  public void shouldReturnTheDate() {
    assertThat(
        create(date, workspace, command, count).get(ICommandData.DATE),
        is(date));
  }

  @Test
  public void shouldReturnTheWorkspace() {
    assertThat(
        create(date, workspace, command, count).get(ICommandData.WORKSPACE),
        is(workspace));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfConstructedWithCountOfZero() {
    create(date, workspace, command, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfConstructedWithNegativeCount() {
    create(date, workspace, command, -1);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutACommand() {
    create(date, workspace, null, count);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADate() {
    create(null, workspace, command, count);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAWorkspace() {
    create(date, null, command, count);
  }

  /**
   * @see CommandData#CommandData(LocalDate, WorkspaceStorage, Command, int)
   */
  private CommandData create(LocalDate d,
                             WorkspaceStorage ws,
                             Command cmd,
                             int count) {
    return new CommandData(d, ws, cmd, count);
  }
}
