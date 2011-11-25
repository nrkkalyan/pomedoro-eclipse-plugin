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

import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;
import org.junit.Test;

/**
 * Test for {@link WorkbenchPartLabelProvider}
 */
public class WorkbenchPartLabelProviderTest extends NullLabelProviderTest {

  IViewDescriptor view;
  IEditorDescriptor editor;
  UndefinedWorkbenchPartDescriptor undefined;

  @Test
  public void getForegroundShouldReturnDarkGrayForAnUndefinedWorkbenchPart() {
    Color expected = PlatformUI.getWorkbench().getDisplay()
        .getSystemColor(SWT.COLOR_DARK_GRAY);
    
    assertThat(provider.getForeground(undefined), equalTo(expected));
  }

  @Test
  public void getImageShouldReturnAnImageForAnEditor() {
    assertThat(provider.getImage(editor), notNullValue());
  }

  @Test
  public void getImageShouldReturnAnImageForAnUndefinedWorkbenchPart() {
    assertThat(provider.getImage(undefined), notNullValue());
  }

  @Test
  public void getImageShouldReturnAnImageForAView() {
    assertThat(provider.getImage(view), notNullValue());
  }

  @Test
  public void getTextShouldReturnTheLabelOfAnEditor() {
    assertThat(provider.getText(editor), equalTo(editor.getLabel()));
  }

  @Test
  public void getTextShouldReturnTheLabelOfAnUndefinedWorkbenchPart() {
    assertThat(provider.getText(undefined), equalTo(undefined.getLabel()));
  }

  @Test
  public void getTextShouldReturnTheLabelOfAView() {
    assertThat(provider.getText(view), equalTo(view.getLabel()));
  }

  @Override
  public void setUp() {
    super.setUp();
    undefined = new UndefinedWorkbenchPartDescriptor("12345");
    view = PlatformUI.getWorkbench().getViewRegistry().getViews()[0];
    editor = PlatformUI.getWorkbench().getEditorRegistry()
        .getDefaultEditor("1.txt");
  }

  @Override
  protected NullLabelProvider create() {
    return new WorkbenchPartLabelProvider();
  }
}
