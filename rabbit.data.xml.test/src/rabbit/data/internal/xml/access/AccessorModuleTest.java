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
import rabbit.data.internal.xml.StoreNamesModule;
import rabbit.data.internal.xml.access.AccessorModule;
import rabbit.data.internal.xml.access.CommandDataAccessor;
import rabbit.data.internal.xml.access.FileDataAccessor;
import rabbit.data.internal.xml.access.JavaDataAccessor;
import rabbit.data.internal.xml.access.LaunchDataAccessor;
import rabbit.data.internal.xml.access.PartDataAccessor;
import rabbit.data.internal.xml.access.PerspectiveDataAccessor;
import rabbit.data.internal.xml.access.SessionDataAccessor;
import rabbit.data.internal.xml.access.TaskDataAccessor;

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
 * Tests for {@link AccessorModule}
 */
@RunWith(Parameterized.class)
public class AccessorModuleTest {

  @Parameters
  public static Collection<Object[]> data() {
    // @formatter:off
    return Arrays.asList(new Object[][]{
        {new TypeLiteral<IAccessor<ICommandData>>     () {}, CommandDataAccessor     .class},
        {new TypeLiteral<IAccessor<IFileData>>        () {}, FileDataAccessor        .class},
        {new TypeLiteral<IAccessor<IJavaData>>        () {}, JavaDataAccessor        .class},
        {new TypeLiteral<IAccessor<ILaunchData>>      () {}, LaunchDataAccessor      .class},
        {new TypeLiteral<IAccessor<IPartData>>        () {}, PartDataAccessor        .class},
        {new TypeLiteral<IAccessor<IPerspectiveData>> () {}, PerspectiveDataAccessor .class},
        {new TypeLiteral<IAccessor<ISessionData>>     () {}, SessionDataAccessor     .class},
        {new TypeLiteral<IAccessor<ITaskData>>        () {}, TaskDataAccessor        .class},
    });
    // @formatter:on
  }

  private Injector injector;
  private TypeLiteral<?> interfaceType;
  private Class<?> implClass;

  // Test that the instance retrieved using the given type is and instance of the given class
  public AccessorModuleTest(TypeLiteral<?> interfaceType, Class<?> implClass) {
    this.interfaceType = interfaceType;
    this.implClass = implClass;
    this.injector = Guice.createInjector(new AccessorModule(), new StoreNamesModule());
  }

  @Test
  public void shouldBindTheCorrectAccessor() {
    assertThat(injector.getInstance(Key.get(interfaceType)), instanceOf(implClass));
  }
}
