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

import java.util.Collection;
import java.util.List;

/**
 * An {@link ICategoryProvider} represents a provider that provides
 * {@link ICategory}s. It is used to determine how categories of data is
 * currently structured.
 */
public interface ICategoryProvider {

  /**
   * Gets all the categories supported by this provider.
   * @return All the supported categories.
   */
  Collection<ICategory> getAllSupported();

  /**
   * Gets the categories that are not in use currently.
   * @return The categories that are not in use.
   */
  Collection<ICategory> getUnselected();

  /**
   * Gets the categories that are in use currently. The order of the categories
   * in the returned collection represents how data of each category is ordered.
   * @return The categories that are in use.
   */
  List<ICategory> getSelected();

  /**
   * Sets the selected categories, data of each category will be structure
   * according the order of the categories. Categories not supported will be
   * silently discarded.
   * @param categories The new categories, ordered.
   */
  void setSelected(ICategory... categories);
}
