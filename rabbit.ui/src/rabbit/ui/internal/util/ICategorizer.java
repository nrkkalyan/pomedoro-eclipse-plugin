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

import javax.annotation.Nullable;

/**
 * Categorizes elements into categories.
 */
public interface ICategorizer {

  /**
   * Gets the category the given element belongs to.
   * @param element the element to categorize.
   * @return the category the given element belongs to.
   */
  ICategory getCategory(@Nullable Object element);

  /**
   * Checks whether this categorizer has the given category.
   * @param category the category to check.
   * @return true if this categorizer has the given category, false otherwise.
   */
  boolean hasCategory(ICategory category);
}
