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

import rabbit.data.internal.xml.StoreNamesModule;
import rabbit.data.internal.xml.convert.ConverterModule;
import rabbit.data.internal.xml.merge.MergerModule;
import rabbit.data.store.IStorer;
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

/**
 * Tests for {@link StorerModule}.
 */
@RunWith(Parameterized.class)
public class StorerModuleTest {

  @Parameters
  public static Collection<Object[]> data() {
    // @formatter:off
    return Arrays.asList(new Object[][] {
        {new TypeLiteral<IStorer<CommandEvent>>     () {}, CommandEventStorer     .class},
        {new TypeLiteral<IStorer<FileEvent>>        () {}, FileEventStorer        .class},
        {new TypeLiteral<IStorer<JavaEvent>>        () {}, JavaEventStorer        .class},
        {new TypeLiteral<IStorer<LaunchEvent>>      () {}, LaunchEventStorer      .class},
        {new TypeLiteral<IStorer<PartEvent>>        () {}, PartEventStorer        .class},
        {new TypeLiteral<IStorer<PerspectiveEvent>> () {}, PerspectiveEventStorer .class},
        {new TypeLiteral<IStorer<SessionEvent>>     () {}, SessionEventStorer     .class},
        {new TypeLiteral<IStorer<TaskFileEvent>>    () {}, TaskFileEventStorer    .class},
    });
    // @formatter:on
  }

  private final Injector injector;
  private TypeLiteral<?> storerInterface;
  private Class<?> storerImplementation;

  public StorerModuleTest(TypeLiteral<?> storerInterface, Class<?> storerImplementation) {
    this.storerInterface = storerInterface;
    this.storerImplementation = storerImplementation;
    this.injector = Guice.createInjector(
        new StorerModule(), new StoreNamesModule(), new ConverterModule(), new MergerModule());
  }

  @Test
  public void shouldBindTheCorrectStorer() {
    assertThat(injector.getInstance(Key.get(storerInterface)), instanceOf(storerImplementation));
  }

  @Test
  public void shouldReturnTheSingletonInstance() {
    assertSame(
        injector.getInstance(Key.get(storerInterface)),
        injector.getInstance(Key.get(storerInterface)));
  }
}
