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
package rabbit.data.internal.xml.access;

import static rabbit.data.access.model.ICommandData.COMMAND;
import static rabbit.data.access.model.ICommandData.COUNT;
import static rabbit.data.access.model.ICommandData.DATE;
import static rabbit.data.access.model.ICommandData.WORKSPACE;

import rabbit.data.access.model.ICommandData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.access.CommandDataAccessor;
import rabbit.data.internal.xml.schema.events.CommandEventListType;
import rabbit.data.internal.xml.schema.events.CommandEventType;
import rabbit.data.internal.xml.schema.events.EventListType;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;

import java.util.List;

/**
 * @see CommandDataAccessor
 */
public class CommandDataAccessorTest extends
    AbstractAccessorTest2<ICommandData, CommandEventType, CommandEventListType> {

  @Override
  protected void assertValues(CommandEventType expected,
      LocalDate expectedDate, WorkspaceStorage expectedWs, ICommandData actual) {

    assertThat(actual.get(COMMAND).getId(), is(expected.getCommandId()));
    assertThat(actual.get(COUNT), is(expected.getCount()));
    assertThat(actual.get(DATE), is(expectedDate));
    assertThat(actual.get(WORKSPACE), is(expectedWs));
  }

  @Override
  protected CommandDataAccessor create() {
    return new CommandDataAccessor(DataStore.COMMAND_STORE);
  }

  @Override
  protected CommandEventListType createCategory() {
    return new CommandEventListType();
  }

  @Override
  protected CommandEventType createElement() {
    CommandEventType type = new CommandEventType();
    type.setCommandId("abc");
    type.setCount(10);
    return type;
  }

  @Override
  protected List<CommandEventListType> getCategories(EventListType events) {
    return events.getCommandEvents();
  }

  @Override
  protected List<CommandEventType> getElements(CommandEventListType list) {
    return list.getCommandEvent();
  }
}
