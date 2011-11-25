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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Label provider for resources.
 */
public final class ResourceLabelProvider extends NullLabelProvider {

  private final Color gray;
  private final WorkbenchLabelProvider workbenchLabels;

  public ResourceLabelProvider() {
    workbenchLabels = new WorkbenchLabelProvider();
    gray = PlatformUI.getWorkbench().getDisplay().getSystemColor(
        SWT.COLOR_DARK_GRAY);
  }

  @Override
  public void dispose() {
    super.dispose();
    workbenchLabels.dispose();
  }

  @Override
  public Color getForeground(Object element) {
    if (element instanceof IResource && !((IResource) element).exists()) {
      return gray;
    }
    return super.getForeground(element);
  }

  @Override
  public Image getImage(Object element) {
    if (element instanceof IResource) {
      return workbenchLabels.getImage(element);
    }
    return super.getImage(element);
  }

  @Override
  public String getText(Object element) {
    if (element instanceof IResource) {
      if (element instanceof IFolder) {
        return ((IFolder) element).getProjectRelativePath().toString();
      }
      return workbenchLabels.getText(element);
    }
    return super.getText(element);
  }
}
