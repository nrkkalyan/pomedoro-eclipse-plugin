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

import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

import org.eclipse.swt.SWT;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

public class PerspectiveLabelProviderTest extends NullLabelProviderTest {

  IPerspectiveDescriptor perspective;

  @Test
  public void getForegroundShouldReturnDarkGrayForAnUndefinedPerspective() {
    IPerspectiveDescriptor undefined = new UndefinedPerspectiveDescriptor("abc");
    assertThat(provider.getForeground(undefined), equalTo(PlatformUI
        .getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY)));
  }

  @Test
  public void getImageShouldReturnAnImageForAPerspective() {
    assertThat(provider.getImage(perspective), notNullValue());
  }

  @Test
  public void getTextShouldReturnTheLabelOfAPerspective() {
    assertThat(provider.getText(perspective), equalTo(perspective.getLabel()));
  }

  @Override
  public void setUp() {
    super.setUp();
    perspective = PlatformUI.getWorkbench().getPerspectiveRegistry()
        .getPerspectives()[0];
  }

  @Override
  protected NullLabelProvider create() {
    return new PerspectiveLabelProvider();
  }
}
