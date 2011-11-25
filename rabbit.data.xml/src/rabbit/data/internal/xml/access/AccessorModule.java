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

import rabbit.data.access.IAccessor;
import rabbit.data.access.model.ICommandData;
import rabbit.data.access.model.IFileData;
import rabbit.data.access.model.IJavaData;
import rabbit.data.access.model.ILaunchData;
import rabbit.data.access.model.IPartData;
import rabbit.data.access.model.IPerspectiveData;
import rabbit.data.access.model.ISessionData;
import rabbit.data.access.model.ITaskData;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * Module for accessors.
 */
public class AccessorModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(new TypeLiteral<IAccessor<ICommandData>>() {})     .to(CommandDataAccessor.class);
    bind(new TypeLiteral<IAccessor<IFileData>>() {})        .to(FileDataAccessor.class);
    bind(new TypeLiteral<IAccessor<IJavaData>>() {})        .to(JavaDataAccessor.class);
    bind(new TypeLiteral<IAccessor<ILaunchData>>() {})      .to(LaunchDataAccessor.class);
    bind(new TypeLiteral<IAccessor<IPartData>>() {})        .to(PartDataAccessor.class);
    bind(new TypeLiteral<IAccessor<IPerspectiveData>>() {}) .to(PerspectiveDataAccessor.class);
    bind(new TypeLiteral<IAccessor<ISessionData>>() {})     .to(SessionDataAccessor.class);
    bind(new TypeLiteral<IAccessor<ITaskData>>() {})        .to(TaskDataAccessor.class);
  }

}
