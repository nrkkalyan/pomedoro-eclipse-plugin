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

import rabbit.ui.internal.AbstractTreeContentProvider;
import rabbit.ui.internal.util.PageDescriptor;

import java.util.Collection;

/**
 * Content provider for {@link PageDescriptor}. Accepts input as a collection.
 */
public class PageDescriptorContentProvider extends AbstractTreeContentProvider {

  /**
   * Constructs a new content provider.
   */
  public PageDescriptorContentProvider() {
    super();
  }

  @Override
  public Object[] getChildren(Object element) {
    if (!(element instanceof PageDescriptor)) {
      return EMPTY_ARRAY;
    }
    return ((PageDescriptor) element).getChildren().toArray();
  }

  @Override
  public Object[] getElements(Object inputElement) {
    if (!(inputElement instanceof Collection<?>)) {
      return EMPTY_ARRAY;
    }
    return ((Collection<?>) inputElement).toArray();
  }

  @Override
  public boolean hasChildren(Object o) {
    return (o instanceof PageDescriptor)
        && !((PageDescriptor) o).getChildren().isEmpty();
  }
}