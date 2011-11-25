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
package rabbit.data.internal.access.model;

import rabbit.data.access.model.ILaunchData;
import rabbit.data.access.model.LaunchConfigurationDescriptor;
import rabbit.data.access.model.WorkspaceStorage;

import com.google.common.collect.Sets;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

/**
 * @see LaunchData
 */
public class LaunchDataTest {

  /**
   * Default value is > 1.
   */
  private int count;
  /**
   * Default value is a mutable set containing more than one files.
   */
  private Set<IFile> files;
  private LocalDate date;
  private WorkspaceStorage workspace;
  private LaunchConfigurationDescriptor config;
  private Duration duration;
  
  @Before
  public void before() {
    count = 10;
    date = new LocalDate();
    workspace = new WorkspaceStorage(new Path(""), new Path(""));
    config = new LaunchConfigurationDescriptor("a", "b", "c");
    duration = new Duration(10);
    files = Sets.newHashSet(mock(IFile.class), mock(IFile.class));
  }

  @Test
  public void shouldReturnNullIfKeyIsNull() {
    assertThat(
        create(date, workspace, config, count, duration, files).get(null),
        is(nullValue()));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADate() {
    create(null, workspace, config, count, duration, files);
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAWorkspace() {
    create(date, null, config, count, duration, files);
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutALaunchConfig() {
    create(date, workspace, null, count, duration, files);
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADuration() {
    create(date, workspace, config, count, null, Collections.<IFile>emptySet());
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutASetOfFiles() {
    create(date, workspace, config, count, duration, null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfConstructedWithANegativeCount() {
    create(date, workspace, config, -1, duration, files);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfConstructedWithACountOfZero() {
    create(date, workspace, config, 0, duration, files);
  }
  
  @Test
  public void shouldAcceptWhenConstructedWithACountOfOne() {
    create(date, workspace, config, 1, duration, files);
  }
  
  @Test
  public void shouldReturnTheDate() {
    LaunchData launch = create(date, workspace, config, count, duration, files);
    assertThat(launch.get(ILaunchData.DATE), is(date));
  }
  
  @Test
  public void shouldReturnTheWorkspace() {
    LaunchData launch = create(date, workspace, config, count, duration, files);
    assertThat(launch.get(ILaunchData.WORKSPACE), is(workspace));
  }
  
  @Test
  public void shouldReturnTheLaunchConfig() {
    LaunchData launch = create(date, workspace, config, count, duration, files);
    assertThat(launch.get(ILaunchData.LAUNCH_CONFIG), is(config));
  }
  
  @Test
  public void shouldReturnTheCount() {
    LaunchData launch = create(date, workspace, config, count, duration, files);
    assertThat(launch.get(ILaunchData.COUNT), is(count));
  }
  
  @Test
  public void shouldReturnTheDuration() {
    LaunchData launch = create(date, workspace, config, count, duration, files);
    assertThat(launch.get(ILaunchData.DURATION), is(duration));
  }
  
  @Test
  public void shouldReturnTheFiles() {
    LaunchData launch = create(date, workspace, config, count, duration, files);
    assertThat(launch.get(ILaunchData.FILES), equalTo(files));
  }
  
  @Test(expected = UnsupportedOperationException.class)
  public void shouldReturnTheFilesInAnImmutableSet() {
    LaunchData launch = create(date, workspace, config, count, duration, files);
    launch.get(ILaunchData.FILES).add(mock(IFile.class));
  }

  /**
   * @see LaunchData#LaunchData(LocalDate, WorkspaceStorage, 
   *      LaunchConfigurationDescriptor, int, Duration, Set)
   */
  private LaunchData create(
      LocalDate date, 
      WorkspaceStorage workspace,
      LaunchConfigurationDescriptor config, 
      int count, Duration duration, 
      Set<IFile> files) {
    return new LaunchData(date, workspace, config, count, duration, files);
  }
}
