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
import org.eclipse.ui.IPerspectiveDescriptor;

/**
 * Represents an undefined perspective.
 */
public class UndefinedPerspectiveDescriptor implements IPerspectiveDescriptor {

  private String id;

  /**
   * Constructor.
   * 
   * @param id The id of this perspective.
   * @throws NullPointerException If argument is null.
   */
  public UndefinedPerspectiveDescriptor(String id) {
    checkNotNull(id);
    this.id = id;
  }

  @Override
  public String getDescription() {
    return null;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public ImageDescriptor getImageDescriptor() {
    return null;
  }

  @Override
  public String getLabel() {
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (obj.getClass() != getClass())
      return false;

    return ((UndefinedPerspectiveDescriptor) obj).getId().equals(getId());
  }

  @Override
  public int hashCode() {
    return getId().hashCode();
  }
}
