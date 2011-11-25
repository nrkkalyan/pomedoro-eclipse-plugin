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

import rabbit.data.internal.xml.schema.events.LaunchEventType;
import rabbit.data.store.model.LaunchEvent;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.HashSet;
import java.util.Set;

/**
 * @see LaunchEventConverter
 */
public class LaunchEventConverterTest extends
    AbstractConverterTest<LaunchEvent, LaunchEventType> {

  @Override
  protected LaunchEventConverter createConverter() {
    return new LaunchEventConverter();
  }

  @Override
  public void testConvert() throws Exception {
    DateTime time = new DateTime();
    long duration = 9823;

    String configTypeId = "configTypeId";
    ILaunchConfigurationType configType = mock(ILaunchConfigurationType.class);
    given(configType.getIdentifier()).willReturn(configTypeId);

    String configName = "configName";
    ILaunchConfiguration config = mock(ILaunchConfiguration.class);
    given(config.getName()).willReturn(configName);

    String modeId = ILaunchManager.RUN_MODE;
    ILaunch launch = mock(ILaunch.class);
    given(launch.getLaunchMode()).willReturn(modeId);

    Set<IPath> filePaths = new HashSet<IPath>();
    filePaths.add(new Path("/abc"));
    filePaths.add(new Path("/def"));

    LaunchEvent event = new LaunchEvent(new Interval(time.getMillis(),
        time.getMillis() + duration), launch, config, configType, filePaths);
    LaunchEventType converted = converter.convert(event);

    assertThat(converted.getCount(), is(1));
    assertThat(converted.getTotalDuration(), equalTo(duration));
    assertThat(converted.getLaunchTypeId(), equalTo(configTypeId));
    assertThat(converted.getLaunchModeId(), equalTo(modeId));
    assertThat(converted.getName(), equalTo(configName));
    assertThat(converted.getFilePath().size(), equalTo(filePaths.size()));
    for (IPath path : filePaths) {
      assertTrue(converted.getFilePath().contains(path.toString()));
    }
  }
}
