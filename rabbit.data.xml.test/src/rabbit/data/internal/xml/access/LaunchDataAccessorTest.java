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

import rabbit.data.access.model.ILaunchData;
import rabbit.data.access.model.LaunchConfigurationDescriptor;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.DatatypeUtil;
import rabbit.data.internal.xml.access.LaunchDataAccessor;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventType;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.ILaunchManager;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

/**
 * @see LaunchDataAccessor
 */
public class LaunchDataAccessorTest extends
    AbstractAccessorTest2<ILaunchData, LaunchEventType, LaunchEventListType> {

  @Override
  protected LaunchDataAccessor create() {
    return new LaunchDataAccessor(DataStore.LAUNCH_STORE);
  }

  @Override
  protected LaunchEventListType createCategory() {
    LaunchEventListType type = new LaunchEventListType();
    type.setDate(DatatypeUtil.toXmlDateTime(new GregorianCalendar()));
    return type;
  }

  @Override
  protected LaunchEventType createElement() {
    LaunchEventType type = new LaunchEventType();
    type.setTotalDuration(10);
    type.setLaunchModeId(ILaunchManager.RUN_MODE);
    type.setName("name");
    type.setLaunchTypeId("type");
    type.setCount(1);
    return type;
  }

  @Override
  protected List<LaunchEventType> getElements(LaunchEventListType list) {
    return list.getLaunchEvent();
  }

  @Override
  protected void assertValues(LaunchEventType expected, LocalDate expectedDate,
      WorkspaceStorage expectedWs, ILaunchData actual) {
    assertThat(actual.get(ILaunchData.DATE), is(expectedDate));
    assertThat(actual.get(ILaunchData.WORKSPACE), is(expectedWs));
    assertThat(actual.get(ILaunchData.COUNT), is(expected.getCount()));
    assertThat(actual.get(ILaunchData.DURATION).getMillis(),
        is(expected.getTotalDuration()));

    LaunchConfigurationDescriptor config = actual.get(ILaunchData.LAUNCH_CONFIG);
    assertThat(config.getLaunchModeId(), is(expected.getLaunchModeId()));
    assertThat(config.getLaunchTypeId(), is(expected.getLaunchTypeId()));
    assertThat(config.getLaunchName(), is(expected.getName()));

    Set<IFile> files = actual.get(ILaunchData.FILES);
    Collection<String> paths = Collections2.transform(files,
        new Function<IFile, String>() {
          @Override
          public String apply(IFile file) {
            return file.getFullPath().toPortableString();
          }
        });
    assertThat(paths.size(), is(expected.getFilePath().size()));
    assertThat(paths, hasItems(expected.getFilePath().toArray(new String[0])));
  }

  @Override
  protected List<LaunchEventListType> getCategories(EventListType events) {
    return events.getLaunchEvents();
  }

}
