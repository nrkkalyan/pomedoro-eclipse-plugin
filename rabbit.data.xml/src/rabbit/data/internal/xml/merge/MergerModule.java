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
package rabbit.data.internal.xml.merge;

import rabbit.data.internal.xml.schema.events.CommandEventType;
import rabbit.data.internal.xml.schema.events.FileEventType;
import rabbit.data.internal.xml.schema.events.JavaEventType;
import rabbit.data.internal.xml.schema.events.LaunchEventType;
import rabbit.data.internal.xml.schema.events.PartEventType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;
import rabbit.data.internal.xml.schema.events.SessionEventType;
import rabbit.data.internal.xml.schema.events.TaskFileEventType;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * Binds {@link IMerger} to its implementations.
 */
public class MergerModule extends AbstractModule {

  public MergerModule() {}

  @Override
  protected void configure() {
    bind(new TypeLiteral<IMerger<CommandEventType>>     () {}).to(CommandEventTypeMerger.class);
    bind(new TypeLiteral<IMerger<FileEventType>>        () {}).to(FileEventTypeMerger.class);
    bind(new TypeLiteral<IMerger<JavaEventType>>        () {}).to(JavaEventTypeMerger.class);
    bind(new TypeLiteral<IMerger<LaunchEventType>>      () {}).to(LaunchEventTypeMerger.class);
    bind(new TypeLiteral<IMerger<PartEventType>>        () {}).to(PartEventTypeMerger.class);
    bind(new TypeLiteral<IMerger<PerspectiveEventType>> () {}).to(PerspectiveEventTypeMerger.class);
    bind(new TypeLiteral<IMerger<SessionEventType>>     () {}).to(SessionEventTypeMerger.class);
    bind(new TypeLiteral<IMerger<TaskFileEventType>>    () {}).to(TaskFileEventTypeMerger.class);
  }

}
