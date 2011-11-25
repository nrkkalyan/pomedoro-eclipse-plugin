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

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;

/**
 * Color provider for resources.
 */
public final class ResourceColorProvider extends NullLabelProvider {

  private final Color gray;

  public ResourceColorProvider() {
    gray = PlatformUI.getWorkbench().getDisplay().getSystemColor(
        SWT.COLOR_DARK_GRAY);
  }

  @Override
  public Color getForeground(Object element) {
    if (element instanceof IResource && !((IResource) element).exists()) {
      return gray;
    }
    return super.getForeground(element);
  }
}
