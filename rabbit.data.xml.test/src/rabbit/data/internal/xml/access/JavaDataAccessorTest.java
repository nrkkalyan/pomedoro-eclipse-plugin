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

import static rabbit.data.access.model.IJavaData.DATE;
import static rabbit.data.access.model.IJavaData.DURATION;
import static rabbit.data.access.model.IJavaData.JAVA_ELEMENT;
import static rabbit.data.access.model.IJavaData.WORKSPACE;
import static rabbit.data.internal.xml.DatatypeUtil.toXmlDate;

import rabbit.data.access.model.IJavaData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.access.JavaDataAccessor;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.JavaEventListType;
import rabbit.data.internal.xml.schema.events.JavaEventType;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.jdt.core.JavaCore;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * @see JavaDataAccessor
 */
public class JavaDataAccessorTest extends
    AbstractAccessorTest2<IJavaData, JavaEventType, JavaEventListType> {

  @Override
  protected void assertValues(JavaEventType expected, LocalDate expectedDate,
      WorkspaceStorage expectedWs, IJavaData actual) {
    assertThat(actual.get(DATE), is(expectedDate));
    assertThat(actual.get(WORKSPACE), is(expectedWs));
    assertThat(actual.get(DURATION).getMillis(), is(expected.getDuration()));
    assertThat(actual.get(JAVA_ELEMENT),
        is(JavaCore.create(expected.getHandleIdentifier())));
  }

  @Override
  protected JavaDataAccessor create() {
    return new JavaDataAccessor(DataStore.JAVA_STORE);
  }

  @Override
  protected JavaEventListType createCategory() {
    JavaEventListType type = new JavaEventListType();
    type.setDate(toXmlDate(new LocalDate()));
    return type;
  }

  @Override
  protected JavaEventType createElement() {
    JavaEventType type = new JavaEventType();
    type.setDuration(10);
    type.setHandleIdentifier("=project/src<pkg{Program.java"); // Valid ID
    return type;
  }

  @Override
  protected List<JavaEventListType> getCategories(EventListType events) {
    return events.getJavaEvents();
  }

  @Override
  protected List<JavaEventType> getElements(JavaEventListType list) {
    return list.getJavaEvent();
  }
}
