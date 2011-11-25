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

import rabbit.data.store.IStorer;
import rabbit.data.store.model.CommandEvent;
import rabbit.data.store.model.FileEvent;
import rabbit.data.store.model.JavaEvent;
import rabbit.data.store.model.LaunchEvent;
import rabbit.data.store.model.PartEvent;
import rabbit.data.store.model.PerspectiveEvent;
import rabbit.data.store.model.SessionEvent;
import rabbit.data.store.model.TaskFileEvent;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * Binds {@link IStorer} to its implementations.
 */
public class StorerModule extends AbstractModule {

  /**
   * Constructor.
   */
  public StorerModule() {}

  @Override
  protected void configure() {
    bind(new TypeLiteral<IStorer<CommandEvent>>     () {}).to(CommandEventStorer.class);
    bind(new TypeLiteral<IStorer<FileEvent>>        () {}).to(FileEventStorer.class);
    bind(new TypeLiteral<IStorer<JavaEvent>>        () {}).to(JavaEventStorer.class);
    bind(new TypeLiteral<IStorer<LaunchEvent>>      () {}).to(LaunchEventStorer.class);
    bind(new TypeLiteral<IStorer<PartEvent>>        () {}).to(PartEventStorer.class);
    bind(new TypeLiteral<IStorer<PerspectiveEvent>> () {}).to(PerspectiveEventStorer.class);
    bind(new TypeLiteral<IStorer<SessionEvent>>     () {}).to(SessionEventStorer.class);
    bind(new TypeLiteral<IStorer<TaskFileEvent>>    () {}).to(TaskFileEventStorer.class);
  }

}
