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
package rabbit.ui.internal.util;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * Represents an undefined {@link IWorkbenchPartDescriptor}. The descriptor may
 * have been available before but no longer available now.
 */
public class UndefinedWorkbenchPartDescriptor implements IWorkbenchPartDescriptor {

  private String id;

  /**
   * Constructor.
   * 
   * @param id The id of the descriptor.
   * @throws NullPointerException If argument is null.
   */
  public UndefinedWorkbenchPartDescriptor(String id) {
    checkNotNull(id);
    this.id = id;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (obj.getClass() != getClass())
      return false;

    return ((UndefinedWorkbenchPartDescriptor) obj).getId().equals(getId());
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public ImageDescriptor getImageDescriptor() {
    return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
        ISharedImages.IMG_DEF_VIEW);
  }
  
  @Override
  public String getLabel() {
    return id;
  }

  @Override
  public int hashCode() {
    return getId().hashCode();
  }

  @Override
  public String toString() {
    return "Undefined workbench part: " + getId();
  }
}
