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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

public final class JavaLabelProviderTest extends NullLabelProviderTest {

  @Test
  public void getForegroundShouldReturnDarkGrayForANonExistJavaElement() {
    Color expectedForeground = PlatformUI.getWorkbench().getDisplay()
        .getSystemColor(SWT.COLOR_DARK_GRAY);

    // A method that does not exists in workspace:
    String methodId = "=Proj/src<com.example{MyPlugin.java[MyPlugin~getDefault";
    IJavaElement method = JavaCore.create(methodId);
    assertThat(method, notNullValue());

    assertThat(provider.getForeground(method), equalTo(expectedForeground));
  }

  @Override
  protected NullLabelProvider create() {
    return new JavaLabelProvider();
  }
}
