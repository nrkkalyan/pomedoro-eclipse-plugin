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
package rabbit.data.store.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.joda.time.Interval;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @see LaunchEvent
 */
public class LaunchEventTest extends ContinuousEventTest {

  @Test
  public void constructorShouldCoptyTheFilePaths() {
    Set<IPath> filePaths = new HashSet<IPath>();
    filePaths.add(new Path("/a"));
    filePaths.add(new Path("/b"));
    filePaths.add(new Path("/c"));
    LaunchEvent event = new LaunchEvent(
        new Interval(0, 1),
        mock(ILaunch.class),
        mock(ILaunchConfiguration.class),
        mock(ILaunchConfigurationType.class),
        filePaths);

    filePaths.add(new Path("/Should not effect the collection in the event."));
    assertThat(event.getFilePaths(), not(equalTo(filePaths)));
  }

  @Test(expected = NullPointerException.class)
  public void constructorShouldThrowAnExceptionIfFilePathsAreNull() {
    new LaunchEvent(
        new Interval(0, 1),
        mock(ILaunch.class),
        mock(ILaunchConfiguration.class),
        mock(ILaunchConfigurationType.class),
        null);
  }

  @Test(expected = NullPointerException.class)
  public void constructorShouldThrowAnExceptionIfLaunchConfigurationIsNull() {
    new LaunchEvent(
        new Interval(0, 1),
        mock(ILaunch.class),
        null,
        mock(ILaunchConfigurationType.class),
        Collections.<IPath> emptySet());
  }

  @Test(expected = NullPointerException.class)
  public void constructorShouldThrowAnExceptionIfLaunchConfigurationTypeIsNull() {
    new LaunchEvent(
        new Interval(0, 1),
        mock(ILaunch.class),
        mock(ILaunchConfiguration.class),
        null,
        Collections.<IPath> emptySet());
  }

  @Test(expected = NullPointerException.class)
  public void constructorShouldThrowAnExceptionIfLaunchIsNull() {
    new LaunchEvent(
        new Interval(0, 1),
        null,
        mock(ILaunchConfiguration.class),
        mock(ILaunchConfigurationType.class),
        Collections.<IPath> emptySet());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void getFilePathsShouldReturnAnUnmodifiableCollection() {
    LaunchEvent event = new LaunchEvent(
        new Interval(0, 1),
        mock(ILaunch.class),
        mock(ILaunchConfiguration.class),
        mock(ILaunchConfigurationType.class),
        new HashSet<IPath>());
    event.getFilePaths().add(new Path("/Should throw exception."));
  }

  @Test
  public void getLaunchShouldReturnTheLaunch() {
    ILaunch launch = mock(ILaunch.class);
    LaunchEvent event = new LaunchEvent(
        new Interval(0, 1),
        launch,
        mock(ILaunchConfiguration.class),
        mock(ILaunchConfigurationType.class),
        Collections.<IPath> emptySet());

    assertThat(event.getLaunch(), equalTo(launch));
  }

  @Test
  public void getLaunchConfigurationShouldReturnTheLaunchConfiguration() {
    ILaunchConfiguration config = mock(ILaunchConfiguration.class);
    LaunchEvent event = new LaunchEvent(
        new Interval(0, 1),
        mock(ILaunch.class),
        config,
        mock(ILaunchConfigurationType.class),
        Collections.<IPath> emptySet());

    assertThat(event.getLaunchConfiguration(), equalTo(config));
  }

  @Test
  public void getLaunchConfigurationTypeShouldReturnTheLaunchConfigurationType() {
    ILaunchConfigurationType type = mock(ILaunchConfigurationType.class);
    LaunchEvent event = new LaunchEvent(
        new Interval(0, 1),
        mock(ILaunch.class),
        mock(ILaunchConfiguration.class),
        type,
        Collections.<IPath> emptySet());

    assertThat(event.getLaunchConfigurationType(), equalTo(type));
  }
}