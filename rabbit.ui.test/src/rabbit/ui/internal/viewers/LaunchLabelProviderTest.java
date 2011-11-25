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
package rabbit.ui.internal.viewers;

import rabbit.ui.internal.util.LaunchName;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

/**
 * @see LaunchLabelProvider
 */
public class LaunchLabelProviderTest extends NullLabelProviderTest {

  private ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();

  @Test
  public void getImageShouldReturnAnImageForAValidLaunchMode() {
    ILaunchMode mode = manager.getLaunchModes()[0];
    assertThat(provider.getImage(mode), notNullValue());
  }

  @Test
  public void getImageShouldReturnAnImageForAValidLaunchConfigurationType() {
    ILaunchConfigurationType type = manager.getLaunchConfigurationTypes()[0];
    assertThat(provider.getImage(type), notNullValue());
  }

  @Test
  public void getImageShouldReturnAnImageForALaunchNameWithAValidLaunchConfigurationTypeId() {
    LaunchName launch = new LaunchName("abc",
        manager.getLaunchConfigurationTypes()[0].getIdentifier());
    assertThat(provider.getImage(launch), notNullValue());
  }

  @Test
  public void getTextShouldReturnTheLabelOfALaunchModeWithoutTheShortcutChar() {
    String originalLabel = "&Run";
    String expectedLabel = "Run";

    ILaunchMode mode = mock(ILaunchMode.class);
    given(mode.getLabel()).willReturn(originalLabel);

    assertThat(provider.getText(mode), equalTo(expectedLabel));
  }

  @Test
  public void getTextShouldReturnTheNameOfALaunchConfigurationType() {
    String expectedName = "HelloWorld";

    ILaunchConfigurationType type = mock(ILaunchConfigurationType.class);
    given(type.getName()).willReturn(expectedName);

    assertThat(provider.getText(type), equalTo(expectedName));
  }

  @Test
  public void getTextShouldReturnTheNameOfALaunchNameObject() {
    String expectedName = "Hello";

    LaunchName name = new LaunchName(expectedName, "not important");
    given(name.getLaunchName()).willReturn(expectedName);

    assertThat(provider.getText(name), equalTo(expectedName));
  }

  @Test
  public void getForegroundShouldReturnDarkGrayForAnInvalidLaunchMode() {
    Color expectedColor = PlatformUI.getWorkbench().getDisplay()
        .getSystemColor(SWT.COLOR_DARK_GRAY);

    ILaunchMode mode = mock(ILaunchMode.class);
    given(mode.getIdentifier()).willReturn("noSuchId");

    assertThat(provider.getForeground(mode), equalTo(expectedColor));
  }

  @Test
  public void getForegroundShouldReturnDarkGrayForAnInvalidLaunchConfigurationType() {
    Color expectedColor = PlatformUI.getWorkbench().getDisplay()
        .getSystemColor(SWT.COLOR_DARK_GRAY);

    ILaunchConfigurationType type = mock(ILaunchConfigurationType.class);
    given(type.getIdentifier()).willReturn("noSuchId");

    assertThat(provider.getForeground(type), equalTo(expectedColor));
  }

  @Override
  protected NullLabelProvider create() {
    return new LaunchLabelProvider();
  }
}
