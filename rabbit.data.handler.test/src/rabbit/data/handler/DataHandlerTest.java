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
package rabbit.data.handler;

import rabbit.data.access.model.ICommandData;
import rabbit.data.access.model.IFileData;
import rabbit.data.access.model.IJavaData;
import rabbit.data.access.model.ILaunchData;
import rabbit.data.access.model.IPartData;
import rabbit.data.access.model.IPerspectiveData;
import rabbit.data.access.model.ISessionData;
import rabbit.data.access.model.ITaskData;
import rabbit.data.store.model.CommandEvent;
import rabbit.data.store.model.FileEvent;
import rabbit.data.store.model.JavaEvent;
import rabbit.data.store.model.LaunchEvent;
import rabbit.data.store.model.PartEvent;
import rabbit.data.store.model.PerspectiveEvent;
import rabbit.data.store.model.SessionEvent;
import rabbit.data.store.model.TaskFileEvent;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * @see DataHandler
 */
public class DataHandlerTest {

  @Test
  public void shouldReturnAStorer() {
    assertNotNull(DataHandler.getStorer(PerspectiveEvent.class));
    assertNotNull(DataHandler.getStorer(CommandEvent.class));
    assertNotNull(DataHandler.getStorer(FileEvent.class));
    assertNotNull(DataHandler.getStorer(PartEvent.class));
    assertNotNull(DataHandler.getStorer(SessionEvent.class));
    assertNotNull(DataHandler.getStorer(TaskFileEvent.class));
    assertNotNull(DataHandler.getStorer(LaunchEvent.class));
    assertNotNull(DataHandler.getStorer(JavaEvent.class));
  }

  @Test
  public void shouldReturnAnAccessor() {
    assertNotNull(DataHandler.getAccessor(IPerspectiveData.class));
    assertNotNull(DataHandler.getAccessor(ICommandData.class));
    assertNotNull(DataHandler.getAccessor(IFileData.class));
    assertNotNull(DataHandler.getAccessor(IPartData.class));
    assertNotNull(DataHandler.getAccessor(ISessionData.class));
    assertNotNull(DataHandler.getAccessor(ITaskData.class));
    assertNotNull(DataHandler.getAccessor(ILaunchData.class));
    assertNotNull(DataHandler.getAccessor(IJavaData.class));
  }
}
