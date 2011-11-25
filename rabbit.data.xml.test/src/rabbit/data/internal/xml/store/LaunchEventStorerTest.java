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

import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.convert.LaunchEventConverter;
import rabbit.data.internal.xml.merge.LaunchEventTypeMerger;
import rabbit.data.internal.xml.schema.events.LaunchEventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventType;
import rabbit.data.store.model.LaunchEvent;

import com.google.common.base.Objects;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.HashSet;
import java.util.Set;

/**
 * @see LaunchEventStorer
 */
public class LaunchEventStorerTest extends
    AbstractStorerTest<LaunchEvent, LaunchEventType, LaunchEventListType> {

  @Override
  protected LaunchEventStorer createStorer() {
    return new LaunchEventStorer(new LaunchEventConverter(),
                                 new LaunchEventTypeMerger(),
                                 DataStore.LAUNCH_STORE);
  }

  @Override
  protected LaunchEvent createEvent(DateTime dateTime) {
    long duration = 19823;
    
    ILaunchConfigurationType type = mock(ILaunchConfigurationType.class);
    given(type.getIdentifier()).willReturn("typeId1");
    
    ILaunchConfiguration config = mock(ILaunchConfiguration.class);
    given(config.getName()).willReturn("configName1");
    
    ILaunch launch = mock(ILaunch.class);
    given(launch.getLaunchMode()).willReturn("run1");
    
    Set<IPath> fileIds = new HashSet<IPath>();
    fileIds.add(new Path("/ab1c"));
    fileIds.add(new Path("/d1ef"));
    return new LaunchEvent(new Interval(dateTime, dateTime.plus(duration)),
        launch, config, type, fileIds);
  }

  @Override
  protected LaunchEvent createEventDiff(DateTime dateTime) {
    long duration = 119823;
    
    ILaunchConfigurationType type = mock(ILaunchConfigurationType.class);
    given(type.getIdentifier()).willReturn("typeId2");
    
    ILaunchConfiguration config = mock(ILaunchConfiguration.class);
    given(config.getName()).willReturn("configName2");
    
    ILaunch launch = mock(ILaunch.class);
    given(launch.getLaunchMode()).willReturn("run2");
    
    Set<IPath> fileIds = new HashSet<IPath>();
    fileIds.add(new Path("/1ab1c"));
    return new LaunchEvent(new Interval(dateTime, dateTime.plus(duration)),
        launch, config, type, fileIds);
  }

  @Override
  protected boolean equal(LaunchEventType t1, LaunchEventType t2) {
    return Objects.equal(t1.getLaunchModeId(), t2.getLaunchModeId())
        && Objects.equal(t1.getLaunchTypeId(), t2.getLaunchTypeId())
        && Objects.equal(t1.getName(), t2.getName())
        && t1.getCount() == t2.getCount()
        && t1.getTotalDuration() == t2.getTotalDuration()
        && t1.getFilePath().size() == t2.getFilePath().size()
        && t1.getFilePath().containsAll(t2.getFilePath())
        && t2.getFilePath().containsAll(t1.getFilePath());
  }
}
