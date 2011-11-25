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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * Label provider for perspectives.
 */
public class PerspectiveLabelProvider extends NullLabelProvider {

  private final org.eclipse.ui.model.PerspectiveLabelProvider provider;
  private final Color gray;

  public PerspectiveLabelProvider() {
    provider = new org.eclipse.ui.model.PerspectiveLabelProvider(false);
    gray = PlatformUI.getWorkbench().getDisplay()
        .getSystemColor(SWT.COLOR_DARK_GRAY);
  }

  @Override
  public Image getImage(Object element) {
    if (element instanceof IPerspectiveDescriptor) {
      return provider.getImage(element);
    }
    return super.getImage(element);
  }

  @Override
  public String getText(Object element) {
    if (element instanceof IPerspectiveDescriptor) {
      return provider.getText(element);
    }
    return super.getText(element);
  }

  @Override
  public Color getForeground(Object element) {
    if (element instanceof UndefinedPerspectiveDescriptor) {
      return gray;
    }
    return super.getForeground(element);
  }
}
