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

import rabbit.data.internal.xml.schema.events.CommandEventType;
import rabbit.data.internal.xml.schema.events.FileEventType;
import rabbit.data.internal.xml.schema.events.JavaEventType;
import rabbit.data.internal.xml.schema.events.LaunchEventType;
import rabbit.data.internal.xml.schema.events.PartEventType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;
import rabbit.data.internal.xml.schema.events.SessionEventType;
import rabbit.data.internal.xml.schema.events.TaskFileEventType;
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
 * Binds {@link IConverter} to its implementations.
 */
public class ConverterModule extends AbstractModule {

  public ConverterModule() {}

  @Override
  protected void configure() {
    bind(new TypeLiteral<IConverter<CommandEvent, CommandEventType>>() {})
        .to(CommandEventConverter.class);

    bind(new TypeLiteral<IConverter<FileEvent, FileEventType>>() {})
        .to(FileEventConverter.class);

    bind(new TypeLiteral<IConverter<JavaEvent, JavaEventType>>() {})
        .to(JavaEventConverter.class);

    bind(new TypeLiteral<IConverter<LaunchEvent, LaunchEventType>>() {})
        .to(LaunchEventConverter.class);

    bind(new TypeLiteral<IConverter<PartEvent, PartEventType>>() {})
        .to(PartEventConverter.class);

    bind(new TypeLiteral<IConverter<PerspectiveEvent, PerspectiveEventType>>() {})
        .to(PerspectiveEventConverter.class);

    bind(new TypeLiteral<IConverter<SessionEvent, SessionEventType>>() {})
        .to(SessionEventConverter.class);

    bind(new TypeLiteral<IConverter<TaskFileEvent, TaskFileEventType>>() {})
        .to(TaskFileEventConverter.class);
  }

}
