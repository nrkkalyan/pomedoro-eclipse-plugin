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
package rabbit.data.internal.xml.merge;

import rabbit.data.internal.xml.schema.events.CommandEventType;
import rabbit.data.internal.xml.schema.events.FileEventType;
import rabbit.data.internal.xml.schema.events.JavaEventType;
import rabbit.data.internal.xml.schema.events.LaunchEventType;
import rabbit.data.internal.xml.schema.events.PartEventType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;
import rabbit.data.internal.xml.schema.events.SessionEventType;
import rabbit.data.internal.xml.schema.events.TaskFileEventType;

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
 * Tests for {@link MergerModule}.
 */
@RunWith(Parameterized.class)
public class MergerModuleTest {

  @Parameters
  public static Collection<Object[]> data() {
    // @formatter:off
    return Arrays.asList(new Object[][] {
        {new TypeLiteral<IMerger<CommandEventType>>     () {}, CommandEventTypeMerger     .class},
        {new TypeLiteral<IMerger<FileEventType>>        () {}, FileEventTypeMerger        .class},
        {new TypeLiteral<IMerger<JavaEventType>>        () {}, JavaEventTypeMerger        .class},
        {new TypeLiteral<IMerger<LaunchEventType>>      () {}, LaunchEventTypeMerger      .class},
        {new TypeLiteral<IMerger<PartEventType>>        () {}, PartEventTypeMerger        .class},
        {new TypeLiteral<IMerger<PerspectiveEventType>> () {}, PerspectiveEventTypeMerger .class},
        {new TypeLiteral<IMerger<SessionEventType>>     () {}, SessionEventTypeMerger     .class},
        {new TypeLiteral<IMerger<TaskFileEventType>>    () {}, TaskFileEventTypeMerger    .class},
    });
    // @formatter:on
  }
  
  private final Injector injector;
  private TypeLiteral<?> mergerInterface;
  private Class<?> mergerImpl;
  
  public MergerModuleTest(TypeLiteral<?> mergerInterface, Class<?> mergerImpl) {
    this.mergerInterface = mergerInterface;
    this.mergerImpl = mergerImpl;
    this.injector = Guice.createInjector(new MergerModule());
  }
  
  @Test
  public void shouldBindTheCorrectMerger() {
    assertThat(injector.getInstance(Key.get(mergerInterface)), instanceOf(mergerImpl));
  }
}
