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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

/**
 * Tests for {@link ConverterModule}.
 */
@RunWith(Parameterized.class)
public class ConverterModuleTest {

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
        {
            new TypeLiteral<IConverter<CommandEvent, CommandEventType>>() {},
            CommandEventConverter.class},
        {
            new TypeLiteral<IConverter<FileEvent, FileEventType>>() {},
            FileEventConverter.class},
        {
            new TypeLiteral<IConverter<JavaEvent, JavaEventType>>() {},
            JavaEventConverter.class},
        {
            new TypeLiteral<IConverter<LaunchEvent, LaunchEventType>>() {},
            LaunchEventConverter.class},
        {
            new TypeLiteral<IConverter<PartEvent, PartEventType>>() {},
            PartEventConverter.class},
        {
            new TypeLiteral<IConverter<PerspectiveEvent, PerspectiveEventType>>() {},
            PerspectiveEventConverter.class},
        {
            new TypeLiteral<IConverter<SessionEvent, SessionEventType>>() {},
            SessionEventConverter.class},
        {
            new TypeLiteral<IConverter<TaskFileEvent, TaskFileEventType>>() {},
            TaskFileEventConverter.class},
    });
  }

  private final Injector injector;
  private TypeLiteral<?> converterInterface;
  private Class<?> converterImpl;

  public ConverterModuleTest(TypeLiteral<?> converterInterface, Class<?> converterImplementation) {
    this.converterInterface = converterInterface;
    this.converterImpl = converterImplementation;
    this.injector = Guice.createInjector(new ConverterModule());
  }

  @Test
  public void shouldBindTheCorrectConverter() {
    assertThat(injector.getInstance(Key.get(converterInterface)), instanceOf(converterImpl));
  }
}
